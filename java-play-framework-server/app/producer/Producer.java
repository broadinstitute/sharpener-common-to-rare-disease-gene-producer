package producer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import apimodels.Attribute;
import apimodels.GeneInfo;
import apimodels.Parameter;
import apimodels.Property;
import apimodels.TransformerInfo;
import apimodels.TransformerQuery;

public class Producer {

	private static final String OMIM_DISEASE_ID = "omim_disease_id";

	private static HashMap<String,ArrayList<GeneInfo>> geneSets = new HashMap<String,ArrayList<GeneInfo>>();

	public static TransformerInfo transformerInfo() {
		TransformerInfo transformerInfo = new TransformerInfo().name("Common-rare disease gene-list producer");
		transformerInfo.function(TransformerInfo.FunctionEnum.fromValue("producer"));
		transformerInfo.addParametersItem(new Parameter().name(OMIM_DISEASE_ID).type(Parameter.TypeEnum.fromValue("string")));
		return transformerInfo;
	}


	public static List<GeneInfo> produceGeneSet(final TransformerQuery query) {

		String ID = "";
		for (Property property : query.getControls()) {
			if (OMIM_DISEASE_ID.equals(property.getName())) {
				ID = property.getValue();
			}
		}
		
		ArrayList<GeneInfo> genes = new ArrayList<GeneInfo>();
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"perl", "scripts/produceGeneListWithWF1.pl", ID};

		try {
			Process proc = rt.exec(commands);
			System.err.println("ID:"+ID);
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			//Read the gene list from WF1
			String s;
			while ((s = stdInput.readLine()) != null) {
				GeneInfo gene = new GeneInfo().geneId("NCBIgene:" + s);
				gene.addAttributesItem(new Attribute().name("entrez_gene_id").value(s).source("BioThings"));
				genes.add(gene); 
				System.err.println(gene);
			}

			//Print any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}
		}
		catch(Exception e) {
			System.err.println(e.toString()); 
		}

		return genes;
	}

}
