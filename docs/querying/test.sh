curl "http://localhost:8888/druid/v2/sql" \
--header 'Content-Type: application/json' \
--data '{
    "query": "SELECT \"arrayDouble\" FROM \"array_example\" WHERE ARRAY_CONTAINS(\"arrayDouble\", ?)",
    "parameters": [{"type": "ARRAY", "value": [999.0, null, 5.5]}] 
}'
