package com.goryaninaa.winter.web.http.server.request.handler;

import java.util.concurrent.atomic.AtomicBoolean;

public interface HandlerCofigurator {
	Manager getManager();


	Security getSecurity();

	AtomicBoolean isSecurityEnabled();
}
