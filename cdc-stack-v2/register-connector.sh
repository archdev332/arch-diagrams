#!/bin/bash

# List all plugins to check if our connector is available
echo "Checking available connector plugins..."
curl -s http://localhost:8083/connector-plugins | jq '.'

echo "Checking if connector exists..."
CONNECTOR_CHECK=$(curl -s http://localhost:8083/connectors/postgres-connector)

if [[ $CONNECTOR_CHECK == *"error_code"* || $CONNECTOR_CHECK == "" ]]; then
  echo "Creating PostgreSQL connector..."

  # Create the connector with detailed logging
  RESPONSE=$(curl -s -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
    "name": "postgres-connector",
    "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "password",
    "database.dbname": "mydb",
    "database.server.name": "postgres-server",
    "table.include.list": "public.users,public.accounts",
    "slot.name": "debezium_slot",
    "publication.name": "dbz_publication",
    "topic.prefix": "cdc-events",
    "topic.naming.strategy": "io.debezium.schema.SchemaTopicNamingStrategy",
    "plugin.name": "pgoutput",
    "flush.lsn.source": "true",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter"
    }
  }')

  echo "Connector creation response:"
  echo $RESPONSE | jq '.'
else
  echo "Connector already exists."
fi

# List current connectors
echo "Current connectors:"
curl -s http://localhost:8083/connectors | jq '.'

# Check connector status if it exists
if [[ $CONNECTOR_CHECK != *"error_code"* && $CONNECTOR_CHECK != "" ]]; then
  echo "Connector status:"
  curl -s http://localhost:8083/connectors/postgres-connector/status | jq '.'
fi

# Print log locations for debugging
echo "For more debugging information, check these logs:"
echo "- Kafka Connect logs: docker logs kafka-connect"
echo "- Connector plugin directory structure:"
ls -la /usr/share/confluent-hub-components