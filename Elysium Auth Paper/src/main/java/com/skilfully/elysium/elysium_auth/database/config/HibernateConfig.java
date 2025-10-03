package com.skilfully.elysium.elysium_auth.database.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import java.util.Properties;

public class HibernateConfig {

    /**
     * 创建 MySQL 数据库的 SessionFactory
     */
    public static SessionFactory createMySQLSessionFactory(
            String host, String database,
            String username, String password, boolean ssl,
            Class<?>... entityClasses) {

        Properties settings = new Properties();

        // JDBC 连接配置
        settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        String url = String.format("jdbc:mysql://%s/%s", host, database);
        if (ssl) {
            url += "?useSSL=true&requireSSL=true";
        } else {
            url += "?useSSL=false";
        }
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, username);
        settings.put(Environment.PASS, password);

        // Hibernate 配置
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.FORMAT_SQL, "true");
        settings.put(Environment.HBM2DDL_AUTO, "update");

        // 连接池配置
        settings.put(Environment.C3P0_MIN_SIZE, "5");
        settings.put(Environment.C3P0_MAX_SIZE, "20");
        settings.put(Environment.C3P0_TIMEOUT, "1800");

        return buildSessionFactory(settings, entityClasses);
    }

    /**
     * 创建 PostgreSQL 数据库的 SessionFactory
     */
    public static SessionFactory createPostgreSQLSessionFactory(
            String host, String database,
            String username, String password, boolean ssl,
            Class<?>... entityClasses) {

        Properties settings = new Properties();

        // JDBC 连接配置
        settings.put(Environment.DRIVER, "org.postgresql.Driver");
        String url = String.format("jdbc:postgresql://%s/%s", host, database);
        if (ssl) {
            url += "?ssl=true&sslmode=require";
        }
        settings.put(Environment.URL, url);
        settings.put(Environment.USER, username);
        settings.put(Environment.PASS, password);

        // Hibernate 配置
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.FORMAT_SQL, "true");
        settings.put(Environment.HBM2DDL_AUTO, "update");

        return buildSessionFactory(settings, entityClasses);
    }

    /**
     * 创建 SQLite 数据库的 SessionFactory
     */
    public static SessionFactory createSQLiteSessionFactory(
            String dbFilePath, Class<?>... entityClasses) {

        Properties settings = new Properties();

        // JDBC 连接配置
        settings.put(Environment.DRIVER, "org.sqlite.JDBC");
        settings.put(Environment.URL, "jdbc:sqlite:" + dbFilePath);

        // Hibernate 配置
        settings.put(Environment.DIALECT, "org.sqlite.hibernate.dialect.SQLiteDialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.FORMAT_SQL, "true");
        settings.put(Environment.HBM2DDL_AUTO, "update");

        // SQLite 特定配置
        settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.connection.C3P0ConnectionProvider");

        return buildSessionFactory(settings, entityClasses);
    }

    /**
     * 通用的 SessionFactory 构建方法
     */
    private static SessionFactory buildSessionFactory(Properties settings, Class<?>... entityClasses) {
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(settings)
                    .build();

            MetadataSources metadataSources = new MetadataSources(standardRegistry);

            // 注册实体类
            for (Class<?> entityClass : entityClasses) {
                metadataSources.addAnnotatedClass(entityClass);
            }

            Metadata metadata = metadataSources.getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();

        } catch (Exception ex) {
            System.err.println("SessionFactory 创建失败: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * 关闭 SessionFactory
     */
    public static void shutdown(SessionFactory sessionFactory) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}