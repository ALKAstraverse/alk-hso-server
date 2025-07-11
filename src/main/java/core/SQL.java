package core;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;

public class SQL {

    public static boolean is_connected = false;
    private static SQL instance = null;
    private HikariDataSource dataSource;
    public final String url;
    private final String user;
    private final String pass;

    public SQL() {
		url = "jdbc:mysql://" + Manager.gI().mysql_host + ":3306/" + Manager.gI().mysql_database
		      + "?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
		System.out.println(url);
		user = Manager.gI().mysql_user;
		pass = Manager.gI().mysql_pass;
		HikariDataSource config = new HikariDataSource();
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(pass);
		config.setAutoCommit(false);
		//
		config.setConnectionTimeout(30_000L);
		config.setIdleTimeout(600_000);
		config.setKeepaliveTime(0);
		config.setMaxLifetime(1_800_000);
		config.setMaximumPoolSize(10);
		config.setPoolName("HSO_pool");
		//
		// config.setLeakDetectionThreshold(10_000L);
		// config.setMaxLifetime(1000L);
		//
		// config.addDataSourceProperty("cachePrepStmts", "true");
		// config.addDataSourceProperty("prepStmtCacheSize", "250");
		// config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		//
		dataSource = new HikariDataSource(config);
		SQL.is_connected = true;
		System.out.println("OPEN DataBase connect");
	}

    public static SQL gI() {
        if (instance == null) {
            instance = new SQL();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (SQL.is_connected) {
            if (dataSource != null) {
                 dataSource.close();
                System.out.println("CLOSE DataBase connect");
            }
            SQL.is_connected = false;
        }
    }
}
