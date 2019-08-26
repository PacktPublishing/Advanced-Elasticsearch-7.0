from elasticsearch.client import Elasticsearch
import threading
from elasticsearch_dsl import connections


class ESLowLevelClientByConnection:
    __conn = None
    __conn_lock = threading.Lock()

    @staticmethod
    def get_instance():
        if ESLowLevelClientByConnection.__conn is None:
            with ESLowLevelClientByConnection.__conn_lock:
                if ESLowLevelClientByConnection.__conn is None:
                    ESLowLevelClientByConnection.__conn = connections.create_connection('high_level_client', hosts=['localhost'], port=9200)
        return ESLowLevelClientByConnection.__conn

    def __init__(self):
        raise Exception("This class is a singleton!, use static method getInstance()")


if __name__ == "__main__":
    test3 = ESLowLevelClientByConnection.get_instance()
    print("test3", hex(id(test3)))

    test4 = ESLowLevelClientByConnection.get_instance()
    print("test4", hex(id(test4)))
