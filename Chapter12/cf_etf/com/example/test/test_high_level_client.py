import unittest
from com.example.client.config.low_level_client_by_connection import ESLowLevelClientByConnection
from elasticsearch_dsl import Search
from elasticsearch_dsl.query import Q, MatchPhrase



class TestHighLevelClientSearch(unittest.TestCase):

    def test_match_phrase_query_via_low_level_client(self):
        # call the query method
        search = Search(index='cf_etf', using=ESLowLevelClientByConnection.get_instance()).query(
            'match_phrase', fund_name='iShares MSCI ACWI ETF')
        response = search.execute()
        self.assertEqual(response['hits']['total']['value'], 1)
        print(response.to_dict())

    def test_match_phrase_query_via_connection(self):
        ESLowLevelClientByConnection.get_instance()
        search = Search(index='cf_etf', using='high_level_client')
        # call the Q method
        search.query = Q('match_phrase', fund_name='iShares MSCI ACWI ETF')
        response = search.execute()
        self.assertEqual(response['hits']['total']['value'], 1)

    def test_match_phrase_class_via_connection(self):
        ESLowLevelClientByConnection.get_instance()
        # construct the query object using the class of the query type
        search = Search(index='cf_etf', using='high_level_client')
        search.query = MatchPhrase(fund_name='iShares MSCI ACWI ETF')
        response = search.execute()
        self.assertEqual(response['hits']['total']['value'], 1)
