package com.goryaninaa.winter.connection.pool;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"squid:S108", "squid:S2445", "squid:S1121",
		"SynchronizationOnLocalVariableOrMethodParameter", "unused"})
public class BasicConnectionPool implements ConnectionPool {

	private static final Logger logger =
			LoggingMech.getLogger(BasicConnectionPool.class.getCanonicalName());
	private final Queue<Connection> connectionPool = new ConcurrentLinkedQueue<>();
	private final List<Connection> usedConnections = new CopyOnWriteArrayList<>();
	private final String dbUrl;
	private final int size;

	public BasicConnectionPool(final Properties prop) {
		dbUrl = prop.getProperty("db.url");
		size = Integer.parseInt(prop.getProperty("Winter.ConnectionPool.size"));
		initializeConnections();
	}

	@Override
	public Connection getConnection() {
		Connection con = null;
		while (con == null) {
			try {
				synchronized (Objects.requireNonNull(con = connectionPool.poll())) {
					usedConnections.add(con);
				}
			} catch (NullPointerException ignored) {

			}
		}
		return con;
	}

	@Override
	public boolean releaseConnection(final Connection con) {
		synchronized (con) {
			try {
				if (con.isClosed()) {
					Connection newCon = DriverManager.getConnection(dbUrl);
					connectionPool.add(newCon);
				} else {
					connectionPool.add(con);
				}
				return usedConnections.remove(con);
			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error(StackTraceString.get(e));
				}
				throw new ConnectionPoolException(e);
			}
		}
	}

	private void initializeConnections() {
		for (int i = 0; i < size; i++) {
			try {
				final Connection con = DriverManager.getConnection(dbUrl);
				connectionPool.add(con);
			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error(StackTraceString.get(e));
				}
				throw new ConnectionPoolException(e);
			}
		}
	}
}
