---
name: MySQL Executor
description: Execute MySQL commands with automatic credential loading from backend/.env. Supports SQL files, inline queries, and interactive shell. Use this skill when the user asks to query the database, execute SQL, or check database schema.
---

# MySQL Executor Skill

Execute MySQL commands for the Growing project database with automatic credential loading.

## Usage

The skill accepts three modes of operation:

1. **Execute SQL file**:
   - User provides a file path
   - Example: "run the migration file database/migrations/V1_create_users.sql"

2. **Execute inline query**:
   - User provides a SQL query string
   - Example: "show me all users"
   - Example: "query the database: SELECT * FROM users LIMIT 10"

3. **Interactive shell**:
   - User asks to open MySQL shell
   - Example: "open mysql shell"
   - Example: "connect to the database"

## Implementation Steps

When this skill is invoked:

1. **Locate MySQL Client**:
   ```bash
   MYSQL_CLIENT=$(brew --prefix mysql-client)/bin/mysql
   ```

2. **Load Credentials**:
   ```bash
   source backend/.env
   # Loads: DB_URL, DB_USERNAME, DB_PASSWORD
   ```

3. **Parse Connection Details from DB_URL**:
   ```bash
   # Example DB_URL: jdbc:mysql://10.0.0.7:37719/growing?useSSL=false...
   # Extract: host=10.0.0.7, port=37719, database=growing

   # Parse host (between // and :)
   DB_HOST=$(echo "$DB_URL" | sed -n 's|.*://\([^:]*\):.*|\1|p')

   # Parse port (between : and /)
   DB_PORT=$(echo "$DB_URL" | sed -n 's|.*://[^:]*:\([^/]*\)/.*|\1|p')

   # Parse database name (between / and ?)
   DB_NAME=$(echo "$DB_URL" | sed -n 's|.*/\([^?]*\).*|\1|p')
   ```

4. **Execute Command**:
   ```bash
   # For SQL file
   $MYSQL_CLIENT -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME < file.sql

   # For inline query
   $MYSQL_CLIENT -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME -e "QUERY"

   # For interactive shell
   $MYSQL_CLIENT -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME
   ```

## Common SQL Queries

### Schema Inspection
```sql
-- List all tables
SHOW TABLES;

-- Describe table structure
DESCRIBE users;
DESCRIBE career_paths;
DESCRIBE user_career_paths;

-- Show table creation
SHOW CREATE TABLE users;
```

### Data Queries
```sql
-- View all users
SELECT * FROM users;

-- View users with their career paths
SELECT u.username, u.email, cp.name as career_path
FROM users u
LEFT JOIN user_career_paths ucp ON u.id = ucp.user_id
LEFT JOIN career_paths cp ON ucp.career_path_id = cp.id;

-- Check career paths
SELECT * FROM career_paths ORDER BY name;

-- Count users by career path
SELECT cp.name, COUNT(ucp.user_id) as user_count
FROM career_paths cp
LEFT JOIN user_career_paths ucp ON cp.id = ucp.career_path_id
GROUP BY cp.id, cp.name;
```

## Error Handling

- **Missing .env file**: Show error and instruct to create backend/.env
- **MySQL client not found**: Suggest `brew install mysql-client`
- **Connection failed**: Display parsed connection details for debugging
- **SQL error**: Show full error message and query context

## Prerequisites

- MySQL client installed: `brew install mysql-client`
- Database credentials in `backend/.env`
- Must run from project root directory

## Database Information

- **Database Name**: growing
- **Tables**: users, career_paths, user_career_paths
- **Shared Server**: Same MySQL server as finance/zjutennis projects but different schema
