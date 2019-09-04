package controllers;

import apimodels.GeneInfo;
import apimodels.TransformerInfo;
import apimodels.TransformerQuery;

import play.mvc.Http;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.validation.constraints.*;

@SuppressWarnings("RedundantThrows")
public interface TransformerApiControllerImpInterface {
    List<GeneInfo> transformPost(TransformerQuery query) throws Exception;

    TransformerInfo transformerInfoGet() throws Exception;

}
