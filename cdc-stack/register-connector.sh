#!/bin/bash

echo "Checking if connector already exists..."
CONNECTOR_EXISTS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/connectors/debezium-connector)

if [ "$CONNECTOR_EXISTS" = "200" ]; then
  echo "Connector already exists. Deleting it first..."
  curl -X DELETE http://localhost:8083/connectors/debezium-connector
  sleep 2
fi

echo "Registering the Debezium PostgreSQL connector..."
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d '{
  "name": "debezium-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "password",
    "database.dbname": "mydb",
    "database.server.name": "postgres-server",
    "table.include.list": "public.users",
    "slot.name": "debezium_slot",
    "publication.name": "dbz_publication",
    "topic.prefix": "cdc-events",
    "plugin.name": "pgoutput",
    "flush.lsn.source": "true",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter"
  }
}'

echo "Connector registration completed."

# Check connector status
sleep 5
echo "Connector status:"
curl -s http://localhost:8083/connectors/debezium-connector/status