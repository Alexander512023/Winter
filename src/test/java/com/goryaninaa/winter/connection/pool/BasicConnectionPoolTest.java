package com.goryaninaa.winter.connection.pool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
class BasicConnectionPoolTest {

	private static ConnectionPool pool;
	private static final int EXPECTED = 1;

	@BeforeAll
	static void init() {
		Properties prop = new Properties();
		prop.setProperty("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		prop.setProperty("Winter.ConnectionPool.size", "1");
		pool = new BasicConnectionPool(prop);
		generateDb();
	}

	@Test
	void getConnectionShouldProvideCorrectConnection() {
		Connection con = pool.getConnection();
		try (ResultSet resultSet = con.prepareStatement("SELECT * FROM test").executeQuery()) {
			resultSet.next();
			int actual = resultSet.getInt(1);
			assertEquals(EXPECTED, actual);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void releaseConnection() {
		int before = pool.getCurrentAmount();
		Connection con = pool.getConnection();
		int conTaken = pool.getCurrentAmount();
		boolean releasedSuccessfully = pool.releaseConnection(con);
		int conReturned = pool.getCurrentAmount();
		boolean testPassed = (before == 1) && (conTaken == 0)
				&& (conReturned == 1) && releasedSuccessfully;
		assertTrue(testPassed);
	}

	@Test
	void connectionPoolShouldCorrectlyHandleConcurrency() throws InterruptedException {
		final List<Integer> actualList = runConcurrentTask();
		boolean queriesCompleted = (actualList.size() == 3);
		boolean resultsCollected = checkForResults(actualList);
		boolean testPassed = (queriesCompleted && resultsCollected);
		assertTrue(testPassed);
	}

	private boolean checkForResults(List<Integer> actualList) {
		boolean resultsCollected = true;
		for (final Integer value : actualList) {
			if (value != 1) {
				resultsCollected = false;
				break;
			}
		}
		return resultsCollected;
	}

	private List<Integer> runConcurrentTask() throws InterruptedException {
		final List<Integer> actualList = new CopyOnWriteArrayList<>();
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		final CountDownLatch latch = new CountDownLatch(3);
		for (int i = 0; i < 3; i++) {
			executor.submit(() -> runSingleQueryTask(actualList, latch));
		}
		latch.await();
		return actualList;
	}

	private void runSingleQueryTask(List<Integer> actualList, CountDownLatch latch) {
		final Connection con = pool.getConnection();
		try (ResultSet resultSet =
					 con.prepareStatement("SELECT * FROM test").executeQuery()) {
			resultSet.next();
			actualList.add(resultSet.getInt(1));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			pool.releaseConnection(con);
			latch.countDown();
		}
	}

	static private void generateDb() {
		try (Connection con =
					 DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
			 Statement stmt = con.createStatement()) {
			stmt.execute("CREATE TABLE test (test_id int);");
			stmt.execute("INSERT INTO test VALUES ('"+ EXPECTED + "');");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}