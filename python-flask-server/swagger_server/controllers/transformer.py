from swagger_server.models.gene_info import GeneInfo
from swagger_server.models.gene_info import GeneInfoIdentifiers
from swagger_server.models.attribute import Attribute
from swagger_server.models.transformer_info import TransformerInfo

import json
import biothings_client

transformer_name = 'Common-to-rare disease genes'
valid_controls = ['omim_disease_id']
control_names = {'omim_disease_id': 'OMIM disease ID'}
default_control_values = {'omim_disease_id': 'MIM:222100'}
default_control_types = {'omim_disease_id': 'string'}


def get_control(controls, control):
    value = controls[control_names[control]] if control_names[control] in controls else default_control_values[control]
    if default_control_types[control] == 'double':
        return float(value)
    elif default_control_types[control] == 'Boolean':
        return bool(value)
    elif default_control_types[control] == 'int':
        return int(value)
    else:
        return value


def entrez_gene_id(gene: GeneInfo):
    """
        Return value of the entrez_gene_id attribute
    """
    if (gene.identifiers is not None and gene.identifiers.entrez is not None):
        if (gene.identifiers.entrez.startswith('NCBIGene:')):
            return gene.identifiers.entrez[9:]
        else:
            return gene.identifiers.entrez
    return None


def transform(query):
    controls = {control.name:control.value for control in query.controls}
    omim_disease_id = get_control(controls, 'omim_disease_id')
    if omim_disease_id.startswith('MIM:'):
        omim_disease_id = omim_disease_id[4:]


    #biothings client
    disease_client = biothings_client.get_client('disease', url='http://mydisease.info/v1')

    #disease -> symptoms
    try:
        symptoms_json = disease_client.querymany(omim_disease_id, scopes='mondo.xrefs.omim', fields='hpo', size = 10000)

        all_symptoms_dic = {}
        for sympton in symptoms_json[0]["hpo"]["phenotype_related_to_disease"]:
        	if sympton["hpo_id"] == 'HP:0000006':
        		continue
        	all_symptoms_dic[sympton["hpo_id"]] = 1

        all_symptoms = sorted (all_symptoms_dic.keys())
    except:
        msg = "Failed to get symptomps for MIM id: '"+omim_disease_id+"'"
        return ({ "status": 404, "title": "Not Found", "detail": msg, "type": "about:blank" }, 404 )

    #symptoms -> diseases
    diseases_json = disease_client.querymany(all_symptoms, scopes='hpo.phenotype_related_to_disease.hpo_id', fields='mondo', size = 10000)

    all_diseases_dic = {}
    for disease in diseases_json:
    	try:
    		if disease['mondo']['xrefs']['omim'] == omim_disease_id:
    			continue
    		all_diseases_dic[disease['mondo']['xrefs']['omim']] = 1
    	except:
    		continue

    all_diseases = sorted (all_diseases_dic.keys())

    #diseases -> genes
    genes_json = disease_client.querymany(all_diseases, scopes='mondo.xrefs.omim', fields='disgenet', size = 10000)

    all_genes_dic = {}
    for gene in genes_json:
    	try:
    		gene["query"]
    		if type (gene["disgenet"]) is list:
    			for disgenet in gene["disgenet"]:
    				try:
    					if type (disgenet["genes_related_to_disease"]):
    						for gene_id in disgenet["genes_related_to_disease"]:
    							try:
    								all_genes_dic[gene_id["gene_id"]] = 1
    							except:
    								continue
    					else:
    						all_genes_dic[disgenet["genes_related_to_disease"]["gene_id"]] = 1
    				except:
    					continue
    		else:
    			if type (gene["disgenet"]["genes_related_to_disease"]) is list:
    				for gene_id in gene["disgenet"]["genes_related_to_disease"]:
    					try:
    						all_genes_dic[gene_id["gene_id"]] = 1
    					except:
    						continue
    			else:
    				all_genes_dic[gene["disgenet"]["genes_related_to_disease"]["gene_id"]] = 1
    	except:
    		continue

    #output gene ids
    output_genes =  sorted (all_genes_dic.keys())
    genes = {}
    gene_list = []
    for gene_id in output_genes:
        if gene_id not in genes:
            gene_entrez_id = "NCBIGene:%s" % gene_id
            gene = GeneInfo(
                gene_id = gene_entrez_id,
                identifiers = GeneInfoIdentifiers(entrez = gene_entrez_id),
                attributes=[Attribute(
                    name = 'related to OMIM disease',
                    value = 'MIM:' + omim_disease_id,
                    source = transformer_name,
                    url = 'https://www.omim.org/entry/' + omim_disease_id
                )]
            )
            genes[entrez_gene_id(gene)] = gene
            gene_list.append(gene)
    return gene_list


def transformer_info():
    """
        Return information for this expander
    """
    global transformer_name, control_names

    with open("transformer_info.json",'r') as f:
        info = TransformerInfo.from_dict(json.loads(f.read()))
        transformer_name = info.name
        control_names = dict((name,parameter.name) for name, parameter in zip(valid_controls, info.parameters))
        return info
