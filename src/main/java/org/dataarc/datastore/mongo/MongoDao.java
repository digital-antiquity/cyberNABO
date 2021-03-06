package org.dataarc.datastore.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.BSONObject;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Combinator;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.QueryPart;
import org.dataarc.core.search.IndexFields;
import org.dataarc.core.service.GeometryWriteConverter;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Shape;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import com.vividsolutions.jts.geom.Geometry;

/**
 * DAO for managing Mongo's interactions
 * @author abrin
 *
 */
@Component
public class MongoDao implements ImportDao, QueryDao {
    private static final String INVALID_QUERY_NO_TYPE = "invalid query (no type)";
    private static final String INVALID_QUERY_NO_FIELD_SPECIFIED = "invalid query (no field specified)";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MongoTemplate template;

    @Autowired
    SchemaDao schemaDao;

    @Autowired
    SourceRepository repository;

    public Map<String, String> getSchema() throws IOException {
        return null;
    }

    private static final String DATA_ENTRY = "dataEntry";

    @Override
    @Transactional(readOnly = false)
    public void save(DataEntry entry) {
        repository.save(entry);
    }

//    @Transactional(readOnly = true)
//    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
//        MongoCursor<String> result = template.getDb().getCollection(DATA_ENTRY).distinct(fieldName, String.class).iterator();
//        // later on if we want to use something like variety.js, we could use this to provide counts
//        Map<String, Long> map = new HashMap<>();
//        while (result.hasNext()) {
//            String r = result.next();
//            map.put(r, 1L);
//        }
//        return map;
//    }

    /**
     * Run a raw MongoDB JSON query
     */
    @Transactional(readOnly = true)
    public FindIterable<Document> runQuery(String query) throws Exception {
        FindIterable<Document> find = template.getDb().getCollection(DATA_ENTRY).find(Document.parse(query));
        return find;
    }

    /**
     * Run a query based on a COmbinator
     */
    @Override
    @Transactional(readOnly = true)
    public FilterQueryResult getMatchingRows(FilterQuery fq, int num_) throws Exception {
        try {
            Query q = getMongoFilterQuery(fq);
            FilterQueryResult result = new FilterQueryResult();
            result.setQuery(fq);
            int i=0;
            List<DataEntry> list = template.find(q, DataEntry.class);
            int num = list.size();
            if (num > num_) {
                num = num_;
            }
            while (i < num ) {
                result.getResults().add(list.get(i));
                i++;
            }
            result.setTotal(list.size());
            return result;
        } catch (QueryException e) {
            String msg = e.getMessage();
            if (INVALID_QUERY_NO_FIELD_SPECIFIED.equals(msg) || INVALID_QUERY_NO_TYPE.equals(msg)) {
                logger.debug("invalid query");
            }
        }
        return null;
    }

    /**
     * Convert a FilterQuery into a MongoQuery
     * @param fq
     * @return
     * @throws QueryException
     */
    private Query getMongoFilterQuery(FilterQuery fq) throws QueryException {
        Query q = new Query();
        Set<String> findAll = schemaDao.findAllSchemaNames();
        Schema schema = null;
        String lookup = fq.getSchema().trim();
        logger.debug("{}", findAll);
        Criteria schemaCriteria = null;

        // limit to a schema
        for (String name : findAll) {
            if (name.toLowerCase().equals(lookup)) {
                schema = schemaDao.findByName(name);
                schemaCriteria = Criteria.where(IndexFields.SOURCE).is(schema.getName());
            }
        }

        Criteria group = new Criteria();
        List<Criteria> criteria = new ArrayList<>();
        // for each condition, create a Where criteria
        for (QueryPart part : fq.getConditions()) {
            if (part.getType() == null) {
                throw new QueryException(INVALID_QUERY_NO_TYPE);
            }
            if (StringUtils.isBlank(part.getFieldName())) {
                throw new QueryException(INVALID_QUERY_NO_FIELD_SPECIFIED);
            }
            // if we're a field-comparison type
            String name = buildFieldName(schema, part.getFieldId(), part.getFieldName());
            if (part.getFieldIdSecond() != null || StringUtils.isNotBlank(part.getFieldNameSecond())) {
                String name2 = buildFieldName(schema, part.getFieldIdSecond(), part.getFieldNameSecond());
                FieldComparisonCriteria crit = new FieldComparisonCriteria();
                crit.setFromName(name);
                crit.setToName(name2);
                crit.setOper(part.getType());
                criteria.add(crit);
                continue;
            }

            if (part.getValue() == "") {
                continue;
            }
            
            // convert our critera type to a mongo criteria
            Criteria where = Criteria.where(name);
            Object value = parse(part.getValue());
            switch (part.getType()) {
                case CONTAINS:
                    where.regex(Pattern.compile(part.getValue(), Pattern.MULTILINE));
                    break;
                case DOES_NOT_EQUAL:
                    where.ne(value);
                    break;
                case EQUALS:
                    where.is(value);
                    break;
                case GREATER_THAN:
                    where.gt(value);
                    break;
                case LESS_THAN:
                    where.lt(value);
                    break;
                default:
                    break;
            }
            criteria.add(where);
        }
        
        // convert our operator
        switch (fq.getOperator()) {
            case AND:
                group = group.andOperator(criteria.toArray(new Criteria[0]));
                break;
            case EXCEPT:
                group = group.norOperator(criteria.toArray(new Criteria[0]));
                break;
            default:
                group = group.orOperator(criteria.toArray(new Criteria[0]));
        }
        if (criteria.size() > 0) {
            q.addCriteria(new Criteria().andOperator(schemaCriteria, group));
        }
        logger.debug(" :: query :: {}", q);
        return q;
    }

    /**
     * Get the field name
     */
    private String buildFieldName(Schema schema, Long fieldId, String fieldName) {
        String name = "properties.";
        for (SchemaField f : schema.getFields()) {
            // these were 'mongoName' but, we shouldn't need that anymore
            if (f.getId() == fieldId) {
                name += f.getName();
            }
            if (Objects.equals(f.getName(), fieldName)) {
                name += f.getName();
            }
        }
        return name;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBySource(String source) {
        repository.deleteBySource(source);
    }

    @Override
    /**
     * Save a Feature in MongoDB
     */
    @Transactional(readOnly = true)
    public void save(Feature feature, Map<String, Object> properties) throws Exception {
        Map<String, Object> props = feature.getProperties();
        String source = (String) props.get(IndexFields.SOURCE);
        // convert the feature back to JSON
        String json = new ObjectMapper().writeValueAsString(feature);

        // create a DataEntry from it
        DataEntry entry = new DataEntry(source, json);
        // append custom values
        appendCustomValues(props, entry);

        // template.save(feature, "dataEntry");
        entry.setProperties(properties);
        remapGeometry(feature, entry);
        repository.save(entry);

    }

    /**
     * Reconcile the GeoJSON and MongoDB geometry structures
     * @param feature
     * @param entry
     */
    private void remapGeometry(Feature feature, DataEntry entry) {
        GeoJsonObject geometry = feature.getGeometry();
        if (geometry instanceof org.geojson.Point) {
            org.geojson.Point point_ = (org.geojson.Point) geometry;
            if (point_.getCoordinates() != null) {
                double latitude = point_.getCoordinates().getLatitude();
                double longitude = point_.getCoordinates().getLongitude();
                entry.setPosition(new GeoJsonPoint(longitude, latitude));
            }
        }
    }

    /**
     * Add our start/title/date etc. fields
     * @param props
     * @param entry
     */
    private void appendCustomValues(Map<String, Object> props, DataEntry entry) {
        entry.setEnd(parseIntProperty(props.getOrDefault("End", props.get(IndexFields.END))));
        Object title = props.getOrDefault("Title", props.get(IndexFields.TITLE));
        if (title != null && StringUtils.isNotBlank((String) title)) {
            entry.setTitle((String) title);
        }
        entry.setStart(parseIntProperty(props.getOrDefault("Start", props.get(IndexFields.START))));
    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
    }

    /*
     * fixme, optimize with data from schema
     */
    private static Object parse(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
        }

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e1) {
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e2) {
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e3) {
        }
        return str;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DataEntry> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void resetRegions() {
        Query q = new Query();
        UpdateResult updateMulti = template.updateMulti(q, new Update().unset(DataEntry.DATA_ARC_REGION), DataEntry.class);

    }

    @Override
    @Transactional(readOnly = false)
    public void resetTopics(String schemaName) {
        Query q = new Query();
        Schema schema = schemaDao.findByName(schemaName);
        Criteria schemaCriteria = Criteria.where(IndexFields.SOURCE).is(schema.getName());
        q.addCriteria(schemaCriteria);
        Update unset = new Update().unset(DataEntry.INDICATORS).unset(DataEntry.TOPICS).unset(DataEntry.TOPIC_IDENTIFIERS);
        UpdateResult updateMulti = template.updateMulti(q, unset, DataEntry.class);

    }

    @Override
    @Transactional(readOnly = false)
    /**
     * Apply Indicators to the MongoDB database
     */
    public void applyIndicator(Combinator indicator) throws QueryException {
        if (CollectionUtils.isEmpty(indicator.getTopics())) {
            return;
        }
        // get the query
        Query filterQuery = getMongoFilterQuery(indicator.getQuery());

        
        List<String> topics = new ArrayList<>();
        List<String> idents = new ArrayList<>();
        
        // create a list of the topics and ids
        indicator.getTopics().forEach(topc -> {
            topics.add(topc.getName());
            idents.add(topc.getIdentifier());
        });
        
        // create an update that adds the indicator
        Update push = new Update().push(DataEntry.INDICATORS, indicator.getId());
        if (CollectionUtils.isNotEmpty(topics)) {
            push.pushAll(DataEntry.TOPICS, topics.toArray(new String[0]));
        }
        if (CollectionUtils.isNotEmpty(idents)) {
            push.pushAll(DataEntry.TOPIC_IDENTIFIERS, idents.toArray(new String[0]));
        }
        
        // run the update
        UpdateResult updateMulti = template.updateMulti(filterQuery, push, DataEntry.class);
        if (updateMulti.getModifiedCount() > 0) {
            logger.debug("applying indicator {} to {} results", indicator.getId(), updateMulti.getModifiedCount());
        }
    }

    @Override
    @Transactional(readOnly = false)
    /**
     * Updates the MongoDB entries from a Geometry loaded from the GeoJSON (this will be on the map) 
     */
    public void updateRegionFromGeometry(Geometry geometry, String val) {
        Query q = new Query();
        logger.trace("{}", geometry.toText());
        GeoJson convert = GeometryWriteConverter.INSTANCE.convert(geometry);
        logger.trace("{}", convert);
        try {
            createSpacialQuery(q, convert);
            UpdateResult updateMulti = template.updateMulti(q, new Update().addToSet(DataEntry.DATA_ARC_REGION, val), DataEntry.class);
            if (updateMulti.getModifiedCount() > 0) {
                logger.debug("  applying template: {} :: {} updated", val, updateMulti.getModifiedCount());
            }
        } catch (Exception e) {
            logger.error("-------------- {} -------------", val);
            logger.error("{}", e, e);
            logger.debug("{}", geometry.toText());
            logger.debug("{}", convert);
            logger.error("-------------- {} -------------", val);

        }
    }

    private void createSpacialQuery(Query q, GeoJson convert) {
        List<Criteria> list = new ArrayList<>();
        if (convert instanceof GeoJsonMultiPolygon) {
            GeoJsonMultiPolygon multiPolygon = (GeoJsonMultiPolygon) convert;
            multiPolygon.getCoordinates().forEach(poly -> {
                Criteria criteria = Criteria.where(DataEntry.POSITION).within((Shape) poly);
                list.add(criteria);
            });

        } else {
            Criteria criteria = Criteria.where(DataEntry.POSITION).within((Shape) convert);
            list.add(criteria);
        }
        Criteria group = new Criteria();
        group = group.orOperator(list.toArray(new Criteria[0]));
        q.addCriteria(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DataEntry> findBySource(String source) {
        Query q = new Query();
        q.addCriteria(Criteria.where(IndexFields.SOURCE).is(source));
        return template.find(q, DataEntry.class);
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * Find all, but within a spatial limit around iceland 
     */
    public Iterable<DataEntry> findAllWithLimit() {
        Query q = new Query();
        Criteria group = new Criteria();
        List<Criteria> lst = new ArrayList<>();
        // POLYGON((-59.4 74.5, 16.0 74.5, 16.0 50.0, -59.4 50.0, -59.4 74.5))
        GeoJsonPolygon p = new GeoJsonPolygon(new GeoJsonPoint(-59.4, 74.5), new GeoJsonPoint(16.0, 74.5),
                new GeoJsonPoint(16.0, 50.0), new GeoJsonPoint(-59.4, 50.0), new GeoJsonPoint(-59.4, 74.5));
        lst.add(Criteria.where(DataEntry.POSITION).within(p));
        group.andOperator(lst.toArray(new Criteria[0]));

        q.addCriteria(group);
        return template.find(q, DataEntry.class);
    }

    public void updateRaw(Combinator ind) throws QueryException {
        Query query = getMongoFilterQuery(ind.getQuery());
        ind.getQuery().setRaw(query.toString());
        
    }

}
