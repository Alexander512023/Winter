package com.goryaninaa.winter.web.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public interface RequestReader {

	Optional<String> getRequest(final BufferedReader input) throws IOException;
}
