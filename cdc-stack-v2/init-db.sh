#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create the users table
    CREATE TABLE IF NOT EXISTS public.users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(200) UNIQUE NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

    -- Set REPLICA IDENTITY to FULL for complete change data capture
    ALTER TABLE public.users REPLICA IDENTITY FULL;

    -- Insert some sample data
    INSERT INTO public.users (name, email) VALUES
        ('John Doe', 'john@example.com'),
        ('Jane Smith', 'jane@example.com'),
        ('Bob Johnson', 'bob@example.com')
    ON CONFLICT (email) DO NOTHING;

    -- Create the accounts table
    CREATE TABLE IF NOT EXISTS public.accounts (
        id SERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
        balance VARCHAR(255) NOT NULL,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- Set REPLICA IDENTITY to FULL for the accounts table
    ALTER TABLE public.accounts REPLICA IDENTITY FULL;

    -- Insert some sample data for accounts
    INSERT INTO public.accounts (user_id, balance) VALUES
        (1, '1000.00'),
        (2, '2500.50'),
        (3, '750.25')
    ON CONFLICT DO NOTHING;

    -- Create the publication for Debezium
    -- Drop the publication if it exists to avoid errors when re-running this script
    DROP PUBLICATION IF EXISTS dbz_publication;

    -- Create a new publication that includes both tables
    CREATE PUBLICATION dbz_publication FOR TABLE public.users, public.accounts;
EOSQL

echo "Database initialization completed."