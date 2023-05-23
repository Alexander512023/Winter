package com.goryaninaa.winter.web.http.server.assembler;

import com.goryaninaa.winter.web.http.server.exception.ServerException;
import com.goryaninaa.winter.web.http.server.request.handler.HandlerCofigurator;
import com.goryaninaa.winter.web.http.server.request.handler.Security;
import com.goryaninaa.winter.web.http.server.request.handler.configurator.BasicHandlerConfigurator;
import com.goryaninaa.winter.web.http.server.request.handler.security.SessionKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.security.BasicSessionKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.security.SecurityProvider;
import com.goryaninaa.winter.web.http.server.request.reader.BasicRequestReader;
import com.goryaninaa.winter.web.http.server.RequestReader;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Controller;
import com.goryaninaa.winter.web.http.server.RequestHandler;
import com.goryaninaa.winter.web.http.server.Server;
import com.goryaninaa.winter.web.http.server.entity.IncomingRequest;
import com.goryaninaa.winter.web.http.server.entity.OutgoingResponse;
import com.goryaninaa.winter.web.http.server.json.JsonDeserializer;
import com.goryaninaa.winter.web.http.server.request.handler.manager.ControllerKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.manager.BasicControllerKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.manager.BasicManager;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Deserializer;
import com.goryaninaa.winter.web.http.server.request.handler.HttpRequestHandler;
import com.goryaninaa.winter.web.http.server.request.handler.Manager;
import com.goryaninaa.winter.web.http.server.request.handler.RequestPreparator;
import com.goryaninaa.winter.web.http.server.request.handler.ResponsePreparator;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class combines classes of server package in one HTTP server.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class HttpServer {
  private final Server server;
  private final ControllerKeeper controllerKeeper;
  private final Properties properties;

  /**
   * This is constructor that receives properties and controllers to tune
   * underlying classes.
   *
   * @param properties  - Java standard properties object passed from client code
   * @param controllers - List of controllers, implemented on the client code side
   * @throws IOException - thrown if failed to instantiate server socket
   */
  public HttpServer(final Properties properties, final List<Controller> controllers)
      throws IOException {
    this.properties = properties;
    final RequestPreparator inc = new IncomingRequest();
    final ResponsePreparator out = new OutgoingResponse();
    final Deserializer deserializer = new JsonDeserializer();
    this.controllerKeeper = new BasicControllerKeeper(properties);
    final Manager manager = new BasicManager(deserializer, controllerKeeper);
    final SessionKeeper authKeeper = new BasicSessionKeeper();
    final Security security = new SecurityProvider(authKeeper, properties);
    final HandlerCofigurator handlerCofigurator =
            new BasicHandlerConfigurator(manager, security, properties);
    final RequestHandler requestHandler = new HttpRequestHandler(inc, out, handlerCofigurator);
    for (final Controller controller : controllers) {
      controllerKeeper.addController(controller);
    }
    final RequestReader requestReader = new BasicRequestReader();
    this.server = new Server(properties, requestHandler, requestReader);
  }

  public void start() {
    if (properties.getProperty("Winter.HttpServer.Security.Enabled").equals("true")
            && controllerKeeper.containsAuthenticationController()) {
      throw new ServerException("Authentication enabled but not properly configured");
    }
    server.start();
  }
}
