#!/bin/bash
curl -XDELETE localhost:9200/cf_etf_dividend_join
curl -XPUT localhost:9200/cf_etf_dividend_join -H "Content-Type:application/json" --data-binary @cf_etf_dividend_join_mappings.json
curl -XPOST localhost:9200/cf_etf_dividend_join/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_acwf_join.json
curl -XPOST localhost:9200/cf_etf_dividend_join/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_dividend_join_bulk.json
