package com.goryaninaa.winter.web.http.server.request.handler.configurator;

import com.goryaninaa.winter.web.http.server.request.handler.HandlerCofigurator;
import com.goryaninaa.winter.web.http.server.request.handler.Manager;
import com.goryaninaa.winter.web.http.server.request.handler.Security;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHandlerConfigurator implements HandlerCofigurator {

	private final AtomicBoolean securityEnabled;
	private final Manager manager;
	private final Security security;

	public BasicHandlerConfigurator(final Manager manager, final Security security,
									final Properties properties) {
		this.securityEnabled =
				properties.getProperty("Winter.HttpServer.Security.Enabled").equals("true")
				? new AtomicBoolean(true) : new AtomicBoolean(false);
		this.manager = manager;
		this.security = security;
	}

	@Override
	public Manager getManager() {
		return manager;
	}

	@Override
	public Security getSecurity() {
		return security;
	}

	@Override
	public AtomicBoolean isSecurityEnabled() {
		return securityEnabled;
	}
}
