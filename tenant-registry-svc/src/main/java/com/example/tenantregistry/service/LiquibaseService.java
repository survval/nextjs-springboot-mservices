package com.example.tenantregistry.service;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Service for managing database schemas using Liquibase.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiquibaseService {

	private final DataSource dataSource;

	@Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-master.xml}")
	private String changeLogPath;

	/**
	 * Creates a new schema for a tenant and applies Liquibase migrations.
	 * @param schemaName the name of the schema to create
	 * @return true if the schema was created and migrations were applied, false if an
	 * error occurred
	 */
	public boolean createSchema(String schemaName) {
		log.info("Creating schema: {}", schemaName);

		try (Connection connection = dataSource.getConnection()) {
			// Create schema if it doesn't exist
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
				log.info("Schema created: {}", schemaName);
			}

			// Apply Liquibase migrations to the new schema
			applyMigrations(connection, schemaName);

			return true;
		}
		catch (SQLException | LiquibaseException e) {
			log.error("Error creating schema: {}", schemaName, e);
			return false;
		}
	}

	/**
	 * Applies Liquibase migrations to a schema.
	 * @param connection the database connection
	 * @param schemaName the name of the schema
	 * @throws LiquibaseException if an error occurs during migration
	 * @throws SQLException if an error occurs with the database connection
	 */
	private void applyMigrations(Connection connection, String schemaName) throws LiquibaseException, SQLException {
		log.info("Applying migrations to schema: {}", schemaName);

		// Set search path to the tenant schema
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("SET search_path TO " + schemaName);
		}

		// Initialize Liquibase and run the migrations
		Database database = DatabaseFactory.getInstance()
			.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		database.setDefaultSchemaName(schemaName);

		try (Liquibase liquibase = new Liquibase(changeLogPath, new ClassLoaderResourceAccessor(), database)) {

			// Use the schema name as the context to allow for tenant-specific migrations
			liquibase.update(new Contexts(schemaName), new LabelExpression());
			log.info("Migrations applied to schema: {}", schemaName);
		}
	}

	/**
	 * Drops a schema.
	 * @param schemaName the name of the schema to drop
	 * @return true if the schema was dropped, false if an error occurred
	 */
	public boolean dropSchema(String schemaName) {
		log.info("Dropping schema: {}", schemaName);

		try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {

			stmt.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
			log.info("Schema dropped: {}", schemaName);

			return true;
		}
		catch (SQLException e) {
			log.error("Error dropping schema: {}", schemaName, e);
			return false;
		}
	}

	/**
	 * Checks if a schema exists.
	 * @param schemaName the name of the schema to check
	 * @return true if the schema exists
	 */
	public boolean schemaExists(String schemaName) {
		log.info("Checking if schema exists: {}", schemaName);

		try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {

			var rs = stmt
				.executeQuery("SELECT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname = '" + schemaName + "')");

			if (rs.next()) {
				boolean exists = rs.getBoolean(1);
				log.info("Schema {} {}", schemaName, exists ? "exists" : "does not exist");
				return exists;
			}

			return false;
		}
		catch (SQLException e) {
			log.error("Error checking if schema exists: {}", schemaName, e);
			return false;
		}
	}

}