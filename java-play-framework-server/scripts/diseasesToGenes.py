import sys
import json
import biothings_client

diseases = sys.argv[1:]

myDisease = biothings_client.get_client('disease', url='http://mydisease.info/v1')
myJson = myDisease.querymany(diseases, scopes='mondo.xrefs.omim', fields='disgenet', size = 10000)
json_string = json.dumps(myJson, ensure_ascii=True)

print(json_string)
