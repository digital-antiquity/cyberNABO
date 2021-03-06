package org.dataarc.web.controller.schema;

import java.util.Arrays;
import java.util.List;

import org.dataarc.bean.ActionType;
import org.dataarc.bean.ObjectType;
import org.dataarc.bean.schema.CategoryType;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.service.ChangeLogService;
import org.dataarc.core.service.DataFileService;
import org.dataarc.core.service.ImportDataService;
import org.dataarc.core.service.SchemaService;
import org.dataarc.core.service.UserService;
import org.dataarc.util.View;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class SchemaController extends AbstractController {

    @Autowired
    SchemaService schemaService;

    @Autowired
    DataFileService dataFileService;

    @Autowired
    private ChangeLogService changelogservice;

    @Autowired
    ImportDataService importService;

    @ModelAttribute("categories")
    public List<CategoryType> getCategories() {
        return Arrays.asList(CategoryType.values());
    }

    @RequestMapping(path = UrlConstants.LIST_SCHEMA, produces = { "text/html; charset=UTF-8" })
    @JsonView(View.Schema.class)
    public ModelAndView schema() {
        ModelAndView mav = new ModelAndView("schema/list");
        mav.addObject("schema", schemaService.findAll());
        return mav;
    }

    @RequestMapping(path = UrlConstants.VIEW_SCHEMA, method = RequestMethod.GET, produces = { "text/html; charset=UTF-8" })
    @JsonView(View.Schema.class)
    public ModelAndView getSchema(@PathVariable(value = "id", required = true) Long id) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        Schema schema = schemaService.findById(id);
        appendModelValues(mav, schema);
        return mav;
    }

    private void appendModelValues(ModelAndView mav, Schema schema) {
        mav.addObject("schema", schema);
        mav.addObject("startFieldId", schema.getStartFieldId());
        mav.addObject("endFieldId", schema.getEndFieldId());
        mav.addObject("textFieldId", schema.getTextFieldId());
        mav.addObject("files", dataFileService.findBySchemaId(schema.getId()));
    }

    @RequestMapping(path = UrlConstants.VIEW_SCHEMA, method = RequestMethod.POST, produces = { "text/html; charset=UTF-8" })
    public ModelAndView saveSchema(@PathVariable(value = "id", required = true) Long id,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "displayName") String displayName,
            @RequestParam(value = "category") CategoryType category,
            @RequestParam(value = "logoUrl") String logoUrl,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "startFieldId", required = false, defaultValue = "-1") Long startFieldId,
            @RequestParam(value = "endFieldId", required = false, defaultValue = "-1") Long endFieldId,
            @RequestParam(value = "textFieldId", required = false, defaultValue = "-1") Long textFieldId) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        Schema schema = schemaService.findById(id);
        schemaService.updateSchema(schema, displayName, description, logoUrl, url, category, startFieldId, endFieldId, textFieldId);
        appendModelValues(mav, schema);
        return mav;
    }

    @RequestMapping(path = UrlConstants.DELETE_SCHEMA, method = RequestMethod.POST, produces = { "text/html; charset=UTF-8" })
    public ModelAndView deleteSchema(@PathVariable(value = "id", required = true) Long id) throws Exception {
        ModelAndView mav = new ModelAndView("schema/view");
        Schema schema = schemaService.findById(id);
        mav.addObject("schema", schema);
        importService.deleteBySource(schema.getName());
        schemaService.deleteSchema(schema);
        changelogservice.save(ActionType.DELETE, ObjectType.DATA_SOURCE, getUser(), schema.getName());
        return mav;
    }

}
