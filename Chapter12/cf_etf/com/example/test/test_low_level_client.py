import unittest
from com.example.client.config.low_level_client import ESLowLevelClient


class TestLowLevelClientSearch(unittest.TestCase):
    es = ESLowLevelClient.get_instance()

    def test_match_phrase_query(self):
        body={
            "query": {
                "match_phrase": {
                    "fund_name": {
                        "query": "iShares MSCI ACWI ETF"
                    }
                }
            }
        }
        response = self.es.search(index='cf_etf', body=body)
        self.assertEqual(response['hits']['total']['value'], 1)
        print(response)

