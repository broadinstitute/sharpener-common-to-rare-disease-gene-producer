package producer;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;

import apimodels.Attribute;
import apimodels.GeneInfo;
import apimodels.GeneInfoIdentifiers;
import apimodels.Parameter;
import apimodels.Property;
import apimodels.TransformerInfo;
import apimodels.TransformerQuery;

public class Producer {

	private static final String OMIM_DISEASE_ID = "omim_disease_id";
	
	private static final String PRODUCER_NAME = "Common-to-rare disease genes";

	public static TransformerInfo transformerInfo() {
		TransformerInfo transformerInfo = new TransformerInfo().name(PRODUCER_NAME);
		transformerInfo.function(TransformerInfo.FunctionEnum.fromValue("producer"));
		transformerInfo.description("Common-to-rare disease gene-list producer");
		transformerInfo.addParametersItem(new Parameter().name(OMIM_DISEASE_ID).type(Parameter.TypeEnum.STRING)._default("MIM:222100"));
		return transformerInfo;
	}


	public static List<GeneInfo> produceGeneSet(final TransformerQuery query) {

		String ID = "";
		for (Property property : query.getControls()) {
			if (OMIM_DISEASE_ID.equals(property.getName())) {
				ID = property.getValue();
			}
		}
		if (ID.startsWith("MIM:")) {
			ID = ID.substring(4);
		}
		
		ArrayList<GeneInfo> genes = new ArrayList<GeneInfo>();
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"perl", "scripts/produceGeneListWithWF1.pl", ID};

		try {
			Process proc = rt.exec(commands);
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			//Read the gene list from WF1
			String s;
			while ((s = stdInput.readLine()) != null) {
				String geneId = "NCBIGene:" + s;
				GeneInfo gene = new GeneInfo().geneId(geneId);
				gene.identifiers(new GeneInfoIdentifiers().entrez(geneId));
				gene.addAttributesItem(new Attribute().name("related to omim disease").value(ID).source(PRODUCER_NAME));
				genes.add(gene); 
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
