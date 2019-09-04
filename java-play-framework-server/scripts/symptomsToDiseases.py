import sys
import json
import biothings_client

symptoms = sys.argv[1:]

myDisease = biothings_client.get_client('disease', url='http://mydisease.info/v1')
myJson = myDisease.querymany(symptoms, scopes='hpo.phenotype_related_to_disease.hpo_id', fields='mondo', size = 10000)

json_string = json.dumps(myJson, ensure_ascii=True)

print(json_string)
