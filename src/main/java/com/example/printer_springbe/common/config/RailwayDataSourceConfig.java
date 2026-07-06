package com.example.printer_springbe.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("railway")
public class RailwayDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(RailwayDataSourceConfig.class);

    @Bean
    @Primary
    public DataSource railwayDataSource(Environment environment) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (StringUtils.hasText(databaseUrl)) {
            log.info("Railway database: using DATABASE_URL");
            return buildFromDatabaseUrl(databaseUrl.trim());
        }

        String host = environment.getProperty("PGHOST");
        String port = environment.getProperty("PGPORT", "5432");
        String database = environment.getProperty("PGDATABASE");
        String username = environment.getProperty("PGUSER");
        String password = environment.getProperty("PGPASSWORD");

        if (StringUtils.hasText(host) && StringUtils.hasText(database)) {
            log.info("Railway database: using PGHOST ({})", host);
            String sslMode = host.contains("railway.internal") ? "disable" : "require";
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=" + sslMode);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setConnectionTimeout(60_000);
            dataSource.setInitializationFailTimeout(60_000);
            return dataSource;
        }

        throw new IllegalStateException(
                "Railway PostgreSQL not linked. Add DATABASE_URL reference from PostgreSQL service.");
    }

    static HikariDataSource buildFromDatabaseUrl(String databaseUrl) {
        String normalized = databaseUrl.replace("postgres://", "postgresql://");
        URI uri = URI.create(normalized);

        String username = null;
        String password = null;
        if (uri.getUserInfo() != null) {
            String[] parts = uri.getUserInfo().split(":", 2);
            username = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            if (parts.length > 1) {
                password = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }

        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String host = uri.getHost();
        String sslMode = host != null && host.contains("railway.internal") ? "disable" : "require";
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + uri.getPath() + "?sslmode=" + sslMode;

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setConnectionTimeout(60_000);
        dataSource.setInitializationFailTimeout(60_000);
        return dataSource;
    }
}
