#!/bin/bash
curl -XDELETE localhost:9200/cf_etf_dividend_nested
curl -XPUT localhost:9200/cf_etf_dividend_nested -H "Content-Type:application/json" --data-binary @cf_etf_dividend_nested_mappings.json
curl -XPOST localhost:9200/cf_etf_dividend_nested/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_dividend_bulk.json
