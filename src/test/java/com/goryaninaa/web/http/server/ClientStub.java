package com.goryaninaa.web.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is special test class that is used to test server.
 *
 * @author Alex Goryanin
 */
class ClientStub {
  private String request;
  private String response = "";
  private final int port;

  public ClientStub(final int port) {
    this.port = port;
  }

  public void go(final String request) { // NOPMD
    this.request = request;

    try (Socket clientSocket = new Socket("127.0.0.1", port); // NOPMD
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(// NOPMD
            new InputStreamReader(clientSocket.getInputStream()))) {

      out.println(request);
      out.flush();

      while (!in.ready()) { // NOPMD
      }

      while (in.ready()) {
        response += in.readLine() + "\n"; // NOPMD
      }

      response = response.trim();

    } catch (IOException e) {
      e.printStackTrace(); // NOPMD
    }
  }

  public String getRequest() {
    return request;
  }

  public String getResponse() {
    return response;
  }
}
