package com.goryaninaa.winter.web.http.server.assembler;

import com.goryaninaa.winter.web.http.server.request.reader.BasicRequestReader;
import com.goryaninaa.winter.web.http.server.RequestReader;
import com.goryaninaa.winter.web.http.server.request.handler.Controller;
import com.goryaninaa.winter.web.http.server.RequestHandler;
import com.goryaninaa.winter.web.http.server.Server;
import com.goryaninaa.winter.web.http.server.entity.IncomingRequest;
import com.goryaninaa.winter.web.http.server.entity.OutgoingResponse;
import com.goryaninaa.winter.web.http.server.json.JsonDeserializer;
import com.goryaninaa.winter.web.http.server.request.handler.ControllerKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.controller.BasicControllerKeeper;
import com.goryaninaa.winter.web.http.server.request.handler.manager.BasicManager;
import com.goryaninaa.winter.web.http.server.request.handler.Deserializer;
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
    final RequestPreparator inc = new IncomingRequest();
    final ResponsePreparator out = new OutgoingResponse();
    final Deserializer deserializer = new JsonDeserializer();
    final Manager manager = new BasicManager(deserializer);
    final ControllerKeeper controllerKeeper = new BasicControllerKeeper();
    final RequestHandler requestHandler = new HttpRequestHandler(inc, out, manager,
            controllerKeeper);
    for (final Controller controller : controllers) {
      controllerKeeper.addController(controller);
    }
    final RequestReader requestReader = new BasicRequestReader();
    this.server = new Server(properties, requestHandler, requestReader);
  }

  public void start() {
    server.start();
  }
}
