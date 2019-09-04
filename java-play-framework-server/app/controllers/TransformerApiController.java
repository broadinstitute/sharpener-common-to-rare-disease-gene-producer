package controllers;

import apimodels.GeneInfo;
import apimodels.TransformerInfo;
import apimodels.TransformerQuery;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import java.io.File;
import swagger.SwaggerUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.validation.constraints.*;
import play.Configuration;

import swagger.SwaggerUtils.ApiAction;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaPlayFrameworkCodegen", date = "2019-09-04T17:57:08.431Z")

public class TransformerApiController extends Controller {

    private final TransformerApiControllerImpInterface imp;
    private final ObjectMapper mapper;
    private final Configuration configuration;

    @Inject
    private TransformerApiController(Configuration configuration, TransformerApiControllerImpInterface imp) {
        this.imp = imp;
        mapper = new ObjectMapper();
        this.configuration = configuration;
    }


    @ApiAction
    public Result transformPost() throws Exception {
        JsonNode nodequery = request().body().asJson();
        TransformerQuery query;
        if (nodequery != null) {
            query = mapper.readValue(nodequery.toString(), TransformerQuery.class);
            if (configuration.getBoolean("useInputBeanValidation")) {
                SwaggerUtils.validate(query);
            }
        } else {
            throw new IllegalArgumentException("'query' parameter is required");
        }
        List<GeneInfo> obj = imp.transformPost(query);
        if (configuration.getBoolean("useOutputBeanValidation")) {
            for (GeneInfo curItem : obj) {
                SwaggerUtils.validate(curItem);
            }
        }
        JsonNode result = mapper.valueToTree(obj);
        return ok(result);
    }

    @ApiAction
    public Result transformerInfoGet() throws Exception {
        TransformerInfo obj = imp.transformerInfoGet();
        if (configuration.getBoolean("useOutputBeanValidation")) {
            SwaggerUtils.validate(obj);
        }
        JsonNode result = mapper.valueToTree(obj);
        return ok(result);
    }
}
