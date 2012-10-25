try:
    import json
except ImportError:
    import sys
    sys.path.append('simplejson-2.3.3')
    import simplejson as json
    
import urllib



class KBaseNetwork:

    def __init__(self, url):
        if url != None:
            self.url = url

    def getDatasets(self, ParameterList):

        arg_hash = { 'method': 'KBaseNetwork.getDatasets',
                     'params': [ParameterList],
                     'version': '1.1'
                     }

        body = json.dumps(arg_hash)
        resp_str = urllib.urlopen(self.url, body).read()
        resp = json.loads(resp_str)

        if 'result' in resp:
            return resp['result'][0]
        else:
            return None

    def buildNetwork(self, ParameterList):

        arg_hash = { 'method': 'KBaseNetwork.buildNetwork',
                     'params': [ParameterList],
                     'version': '1.1'
                     }

        body = json.dumps(arg_hash)
        resp_str = urllib.urlopen(self.url, body).read()
        resp = json.loads(resp_str)

        if 'result' in resp:
            return resp['result'][0]
        else:
            return None

    def buildFirstNeighborNetwork(self, ParameterList):

        arg_hash = { 'method': 'KBaseNetwork.buildFirstNeighborNetwork',
                     'params': [ParameterList],
                     'version': '1.1'
                     }

        body = json.dumps(arg_hash)
        resp_str = urllib.urlopen(self.url, body).read()
        resp = json.loads(resp_str)

        if 'result' in resp:
            return resp['result'][0]
        else:
            return None

    def buildInternalNetwork(self, ParameterList):

        arg_hash = { 'method': 'KBaseNetwork.buildInternalNetwork',
                     'params': [ParameterList],
                     'version': '1.1'
                     }

        body = json.dumps(arg_hash)
        resp_str = urllib.urlopen(self.url, body).read()
        resp = json.loads(resp_str)

        if 'result' in resp:
            return resp['result'][0]
        else:
            return None




        
