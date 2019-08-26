#!/bin/bash
curl -XDELETE localhost:9200/cf_etf
curl -XPUT localhost:9200/cf_etf -H "Content-Type:application/json" --data-binary @define_custom_analyzer.json
curl -XPOST localhost:9200/cf_etf/_bulk?pretty -H "Content-Type:application/json" --data-binary @cf_etf_list_bulk.json
