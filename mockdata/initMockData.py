import yaml
from peewee import *

def load_conf_file(config_file):
    with open(config_file, "r") as config_file:
        return yaml.safe_load(config_file)
    
def parse_datasource(conf_file) :
    return conf_file['database']



conf_file = load_conf_file('../src/main/resources/security/security-performance.yaml')
datasource = parse_datasource(conf_file)

db_url = datasource['url']
username = datasource['username']
password = datasource['password']

mySqlDb = MySQLDatabase('performance_db', user=username, password=password, host=db_url, port=3306)


mySqlDb.connect()
mySqlDb.close()