package org.dataarc.web.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SearchResultObject implements Serializable {

    private static final long serialVersionUID = -5124555479159066349L;

    private Object results;
    private List<String> idList;

    private Map<String, Map<String, Object>> facets = new HashMap<>();

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    public Map<String, Map<String, Object>> getFacets() {
        return facets;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
