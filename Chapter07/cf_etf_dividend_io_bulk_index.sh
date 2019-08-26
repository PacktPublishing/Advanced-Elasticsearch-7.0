#!/bin/bash
curl -XDELETE localhost:9200/cf_etf_dividend_io
curl -XPUT localhost:9200/cf_etf_dividend_io -H "Content-Type:application/json" -d '{}'
curl -XPOST localhost:9200/cf_etf_dividend_io/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_dividend_bulk.json
