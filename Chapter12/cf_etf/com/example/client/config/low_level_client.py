from elasticsearch.client import Elasticsearch
import threading


class ESLowLevelClient:
    __es = None
    __es_lock = threading.Lock()

    @staticmethod
    def get_instance():
        if ESLowLevelClient.__es is None:
            with ESLowLevelClient.__es_lock:
                if ESLowLevelClient.__es is None:
                    ESLowLevelClient.__es = Elasticsearch(['localhost'], port=9200, maxsize=25)
        return ESLowLevelClient.__es

    def __init__(self):
        raise Exception("This class is a singleton!, use static method getInstance()")


if __name__ == "__main__":
    test3 = ESLowLevelClient.get_instance()
    print("test3", hex(id(test3)))

    test4 = ESLowLevelClient.get_instance()
    print("test4", hex(id(test4)))

