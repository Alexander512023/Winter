package com.goryaninaa.winter.connection.pool;

import java.sql.Connection;

@SuppressWarnings("unused")
public interface ConnectionPool {

	Connection getConnection();
	boolean releaseConnection(Connection connection);

}
