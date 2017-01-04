package org.dataarc.bean;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

//@Entity
//@Table(name="source_data")
//@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class DataEntry {

    private GeoJsonPoint position;

    public DataEntry() {
    }

    @Id
    public String id;

    public DataEntry(String source, String data) {
        this.setSource(source);
        this.setData(data);
    }

    @Column(name = "date_start")
    private Integer start;
    @Column(name = "date_end")
    private Integer end;

    @Column(name = "title")
    private String title;
    
    public GeoJsonPoint getPosition() {
        return position;
    }

    public void setPosition(GeoJsonPoint position) {
        this.position = position;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Column
    // @Type(type="StringJsonObject")
    private String data;

    @Column
    private String source;

    @Column(name = "date_created", nullable = false)
    private Date dateCreated;
    private Map<String, Object> properties;
    private Set<String> indicators;


    @Override
    public String toString() {
        return String.format("%s - %s (%s)", source, title, id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(Set<String> indicators) {
        this.indicators = indicators;
    };
}
