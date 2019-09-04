package controllers;

import apimodels.GeneInfo;
import apimodels.TransformerInfo;
import apimodels.TransformerQuery;

import producer.Producer;

import play.mvc.Http;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaPlayFrameworkCodegen", date = "2019-09-04T17:57:08.431Z")

public class TransformerApiControllerImp implements TransformerApiControllerImpInterface {
    @Override
    public List<GeneInfo> transformPost(TransformerQuery query) throws Exception {
    	return Producer.produceGeneSet(query);
    }

    @Override
    public TransformerInfo transformerInfoGet() throws Exception {
    	return Producer.transformerInfo();
    }

}
