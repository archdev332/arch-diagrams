# Debezium CDC Stack with Kafka and PostgreSQL

This repository contains a complete Change Data Capture (CDC) setup using:
- PostgreSQL as the source database
- Debezium for CDC (custom version 3.0.8 instead of default)
- Kafka (KRaft mode) as the message broker
- Kafka Connect for managing connectors
- Redpanda Console for monitoring and management
- Adminer for database management

## Quick Start

Follow these steps to get the CDC stack up and running:

### 1. Prepare your environment

Create the necessary directories:

```bash
mkdir -p store/postgres store/kafka store/kafka-connect
```

### 2. Prepare connector files

Make the download script executable and run it:

```bash
chmod +x download-connector.sh
./download-connector.sh
```

This will download Debezium PostgreSQL connector version 3.0.8 and its dependencies. The stack is specifically configured to use version 3.0.8 instead of the latest version, which provides better stability and consistency.

### 3. Make scripts executable

```bash
chmod +x register-connector.sh
chmod +x init-db.sh
```

### 4. Start the stack

```bash
docker-compose up -d
```

The first startup might take a few minutes as Docker needs to download the images and initialize the services.

### 5. Verify services

Check if all services are running:

```bash
docker-compose ps
```

All services should be in a "running" state with healthy status.

### 6. Access UIs

- Redpanda Console: http://localhost:8082
    - Use this to monitor Kafka topics, messages, and connectors
- Adminer: http://localhost:8888
    - Login with:
        - System: PostgreSQL
        - Server: postgres
        - Username: postgres
        - Password: password
        - Database: mydb

### 7. Test the CDC functionality

1. Connect to PostgreSQL and make changes to the users or accounts tables:

```bash
docker exec -it postgres-cdc psql -U postgres -d mydb
```

2. Insert or update records:

```sql
INSERT INTO users (name, email) VALUES ('Alice Cooper', 'alice@example.com');
UPDATE accounts SET balance = '3000.00' WHERE user_id = 1;
```

3. Check the data changes in Redpanda Console:
    - Navigate to http://localhost:8082
    - Go to Topics
    - Look for topics with the prefix `cdc-events`

## Troubleshooting

### Check Connector Status

```bash
curl -s http://localhost:8083/connectors/postgres-connector/status | jq '.'
```

### View Connector Logs

```bash
docker logs kafka-connect
```

### Reset the Stack

If you need to start fresh:

```bash
docker-compose down
rm -rf store/kafka/* store/kafka-connect/* store/postgres/*
```

Then follow the setup steps again.

## Configuration Details

### Database Configuration

- The PostgreSQL database is configured with logical replication enabled
- Sample tables (`users` and `accounts`) are created with `REPLICA IDENTITY FULL`
- The `accounts` table has a foreign key relationship to the `users` table, demonstrating relational data capture
- A publication named `dbz_publication` is created for Debezium that includes both tables

### Connector Configuration

- The Debezium PostgreSQL connector uses the `pgoutput` plugin
- CDC events are sent to topics with the prefix `cdc-events`
- The connector is configured to capture changes from multiple tables simultaneously via the `"table.include.list": "public.users,public.accounts"` setting
- This multi-table capture allows tracking related changes across your database schema

## Customizing

### Adding New Tables

1. Modify the `init-db.sh` script to create your table
2. Set `REPLICA IDENTITY FULL` for the table
3. Add the table to the publication
4. Update the `table.include.list` in `register-connector.sh` by adding your table name to the comma-separated list
5. Restart the stack with `docker-compose down && docker-compose up -d`

The setup already demonstrates capturing data from multiple related tables (`users` and `accounts`). You can extend this pattern to capture changes from more tables in your schema.

### Using a Different Debezium Version

1. Update the `DEBEZIUM_VERSION` in `download-connector.sh`
2. Run the download script again
3. Restart the Kafka Connect service: `docker-compose restart kafka-connect`

## License

This project is available under the MIT License.