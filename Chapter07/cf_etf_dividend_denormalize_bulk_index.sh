#!/bin/bash
curl -XDELETE localhost:9200/cf_etf_dividend_denormalize
curl -XPUT localhost:9200/cf_etf_dividend_denormalize -H "Content-Type:application/json" -d '{}'
curl -XPOST localhost:9200/cf_etf_dividend_denormalize/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_dividend_denormalize_bulk.json
