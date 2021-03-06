package org.dataarc.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dataarc.bean.file.JsonFile;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.file.JsonFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Basic methods for dealing with a GeoJSON File (used on the map)
 * @author abrin
 *
 */
@Service
@Transactional
public class JsonFileService {

    @Autowired
    ImportDao sourceDao;
    @Autowired
    JsonFileDao jsonFileDao;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * For each file, apply the IDs to matching MongoDB entries
     */
    @Transactional(readOnly = false)
    public void applyGeoJsonFiles() {
        // for each file
        List<JsonFile> files = jsonFileDao.findAll();
        // clear all region entries
        sourceDao.resetRegions();
        for (JsonFile file : files) {
            try {
                File file_ = new File(file.getPath());
                if (!file_.exists()) {
                    continue;
                }
                logger.debug("applying file: {}", file_);
                // read each feature collection from the file
                FeatureCollection featureCollection = (FeatureCollection) GeoJSONFactory.create(IOUtils.toString(new FileReader(file_)));

                // for each feature...
                for (Feature feature : featureCollection.getFeatures()) {
                    GeoJSONReader reader = new GeoJSONReader();
                    // get the geometry and the id
                    Geometry geometry = reader.read(feature.getGeometry());
                    // apply it to the MongoDB Entry
                    sourceDao.updateRegionFromGeometry(geometry, file.getId() + "_____" + feature.getProperties().get("id"));
                }
            } catch (IOException e) {
                logger.error("erorr indexing spatial facet - {}", e, e);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<JsonFile> findAll() {
        return jsonFileDao.findAll();
    }

    @Transactional(readOnly = true)
    public String findById(Long id) throws FileNotFoundException, IOException {
        JsonFile findById = jsonFileDao.findById(id);
        return IOUtils.toString(new FileInputStream(new File(findById.getPath())), Charset.forName("UTF-8"));
    }

    @Transactional(readOnly = false)
    public void deleteById(Long id) {
        jsonFileDao.deleteById(id);

    }
}
