package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.request.handler.manager.BasicManager;
import java.util.concurrent.atomic.AtomicBoolean;

public class HandlerConfiguratorStub implements HandlerCofigurator{
	@Override
	public Manager getManager() {
		return new ManagerStub();
	}

	@Override
	public Security getSecurity() {
		return new SecurityStub();
	}

	@Override
	public AtomicBoolean isSecurityEnabled() {
		return new AtomicBoolean(false);
	}
}
