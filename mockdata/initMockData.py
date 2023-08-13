import yaml
from peewee import *


def load_conf_file(config_file):
    with open(config_file, "r") as config_file:
        config = yaml.safe_load(config_file)
        print(config)


load_conf_file('../src/main/resources/security/security-release.yaml')
