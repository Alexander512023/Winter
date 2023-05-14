package com.goryaninaa.winter.connection.pool;

@SuppressWarnings("unused")
public class ConnectionPoolException extends RuntimeException{
	public ConnectionPoolException() {
	}

	public ConnectionPoolException(String message) {
		super(message);
	}

	public ConnectionPoolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionPoolException(Throwable cause) {
		super(cause);
	}

	public ConnectionPoolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
