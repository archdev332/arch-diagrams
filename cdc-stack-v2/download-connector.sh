#!/bin/bash
set -e

DEBEZIUM_VERSION="3.0.8.Final"
# Create the directory structure expected by Kafka Connect
# Format: /usr/share/confluent-hub-components/debezium-connector-postgresql/lib/...
TARGET_DIR="./store/kafka-connect/debezium-connector-postgresql"
LIB_DIR="${TARGET_DIR}/lib"
MAVEN_BASE_URL="https://repo1.maven.org/maven2"

# Make sure the directory exists
mkdir -p $LIB_DIR

# Download Debezium core components
echo "Downloading Debezium connector v${DEBEZIUM_VERSION}..."

# Debezium API
wget -O "${LIB_DIR}/debezium-api-${DEBEZIUM_VERSION}.jar" \
  "${MAVEN_BASE_URL}/io/debezium/debezium-api/${DEBEZIUM_VERSION}/debezium-api-${DEBEZIUM_VERSION}.jar"

# Debezium Core
wget -O "${LIB_DIR}/debezium-core-${DEBEZIUM_VERSION}.jar" \
  "${MAVEN_BASE_URL}/io/debezium/debezium-core/${DEBEZIUM_VERSION}/debezium-core-${DEBEZIUM_VERSION}.jar"

# Debezium PostgreSQL Connector (main connector JAR)
wget -O "${LIB_DIR}/debezium-connector-postgres-${DEBEZIUM_VERSION}.jar" \
  "${MAVEN_BASE_URL}/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}.jar"

# Download Debezium Embedded Engine
wget -O "${LIB_DIR}/debezium-embedded-${DEBEZIUM_VERSION}.jar" \
  "${MAVEN_BASE_URL}/io/debezium/debezium-embedded/${DEBEZIUM_VERSION}/debezium-embedded-${DEBEZIUM_VERSION}.jar"

# Download Debezium DDL Parser
wget -O "${LIB_DIR}/debezium-ddl-parser-${DEBEZIUM_VERSION}.jar" \
  "${MAVEN_BASE_URL}/io/debezium/debezium-ddl-parser/${DEBEZIUM_VERSION}/debezium-ddl-parser-${DEBEZIUM_VERSION}.jar"

# PostgreSQL JDBC Driver
POSTGRES_DRIVER_VERSION="42.7.5"
wget -O "${LIB_DIR}/postgresql-${POSTGRES_DRIVER_VERSION}.jar" \
  "${MAVEN_BASE_URL}/org/postgresql/postgresql/${POSTGRES_DRIVER_VERSION}/postgresql-${POSTGRES_DRIVER_VERSION}.jar"

# Additional dependencies
echo "Downloading additional dependencies..."

# Protobuf
PROTOBUF_VERSION="4.30.1"
wget -O "${LIB_DIR}/protobuf-java-${PROTOBUF_VERSION}.jar" \
  "${MAVEN_BASE_URL}/com/google/protobuf/protobuf-java/${PROTOBUF_VERSION}/protobuf-java-${PROTOBUF_VERSION}.jar"

# Create manifest.json file expected by Kafka Connect
echo '{
  "name": "debezium-connector-postgresql",
  "version": "3.0.8",
  "title": "Debezium PostgreSQL CDC Connector",
  "description": "Captures change data from PostgreSQL databases",
  "owner": {
    "username": "debezium",
    "name": "Debezium Community",
    "type": "organization",
    "url": "https://debezium.io"
  },
  "tags": ["debezium", "cdc", "postgres", "postgresql"],
  "features": {
    "supported_encodings": ["any"],
    "single_message_transforms": true,
    "confluent_control_center_integration": true,
    "kafka_connect_api": true
  },
  "documentation_url": "https://debezium.io/documentation/reference/3.0/connectors/postgresql.html",
  "source_url": "https://github.com/debezium/debezium",
  "docker_image": {},
  "license": {
    "name": "Apache License 2.0",
    "url": "https://github.com/debezium/debezium/blob/master/LICENSE.txt"
  },
  "component_types": ["connector"],
  "requirements": ["PostgreSQL"]
}' > "${TARGET_DIR}/manifest.json"

echo "Download complete! Connector files are in ${TARGET_DIR}/lib"
echo "Created the expected directory structure for Kafka Connect"