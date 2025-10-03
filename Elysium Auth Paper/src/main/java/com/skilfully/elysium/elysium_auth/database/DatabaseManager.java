package com.skilfully.elysium.elysium_auth.database;

import com.skilfully.elysium.elysium_auth.database.config.HibernateConfig;
import com.skilfully.elysium.elysium_auth.database.entity.AccountData;
import com.skilfully.elysium.elysium_auth.utils.MessageSender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DatabaseManager {

    private SessionFactory sessionFactory;
    private boolean initialized = false;

    /**
     * 初始化 MySQL 数据库连接
     */
    public void initMySQL(String host, String database,
                          String username, String password, boolean ssl) {
        if (initialized) throw new IllegalStateException("Already initialized");
        this.initialized = true;
        this.sessionFactory = HibernateConfig.createMySQLSessionFactory(
                host, database, username, password, ssl, AccountData.class
        );
    }

    /**
     * 初始化 PostgreSQL 数据库连接
     */
    public void initPostgreSQL(String host, String database,
                               String username, String password, boolean ssl) {
        if (initialized) throw new IllegalStateException("Already initialized");
        this.initialized = true;
        this.sessionFactory = HibernateConfig.createPostgreSQLSessionFactory(
                host, database, username, password, ssl, AccountData.class
        );
    }

    /**
     * 初始化 SQLite 数据库连接
     */
    public void initSQLite(String dbFilePath) {
        if (initialized) throw new IllegalStateException("Already initialized");
        this.initialized = true;
        this.sessionFactory = HibernateConfig.createSQLiteSessionFactory(
                dbFilePath, AccountData.class
        );
    }

    /**
     * 获取 Session
     */
    public Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     * 关闭数据库连接
     */
    public void shutdown() {
        HibernateConfig.shutdown(sessionFactory);
    }

    /**
     * 示例：保存用户
     */
    public void saveAccountData(AccountData user) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            MessageSender.error(e.getMessage());
        }
    }

    /**
     * 示例：根据ID查找用户
     */
    public AccountData findAccountDataById(Long id) {
        try (Session session = getSession()) {
            return session.get(AccountData.class, id);
        }
    }
}