from com.example.client.config.low_level_client_by_connection import ESLowLevelClientByConnection
from elasticsearch_dsl import Search
from elasticsearch_dsl.query import Q, Bool, Range, Term
from elasticsearch_dsl.aggs import A, DateHistogram


def etl(index='cf_rfem_hist_price', start_date='2018-12-26', end_date='2019-03-25', symbol='rfem'):
    ESLowLevelClientByConnection.get_instance()
    search = Search(index=index, using='high_level_client')[0:100]
    search.query = Q(Bool(must=[Range(date={'gte': '2018-12-26', 'lte': '2019-03-25'}), Term(symbol='rfem')]))
    aggs = A(DateHistogram(field='date', interval='1d', format='yyyy-MM-dd', min_doc_count=1))
    response = search.execute()
    hits = response['hits']
    hits=hits['hits']
    XX=[]
    for hit in hits:
      X=[]
      X.append(hit['_source']['changeOverTime'])
      X.append(hit['_source']['changePercent'])
      X.append(hit['_source']['volume'])
      XX.append(X)
    return(XX)

if __name__ == "__main__":
    XX=etl()
    for X in XX:
      print(X)
