#!/bin/bash
curl -XDELETE localhost:9200/cf_etf_suggester
curl -XPUT localhost:9200/cf_etf_suggester -H "Content-Type:application/json" --data-binary @cf_etf_suggester_mappings.json
curl -XPOST localhost:9200/cf_etf_suggester/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_suggester_bulk.json
