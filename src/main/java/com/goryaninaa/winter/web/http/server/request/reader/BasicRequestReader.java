package com.goryaninaa.winter.web.http.server.request.reader;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.web.http.server.RequestReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicRequestReader implements RequestReader {

	private static final int TC_TIME_OUT = 50;
	private static final Logger LOG =
			LoggingMech.getLogger(BasicRequestReader.class.getCanonicalName());

	@Override
	public Optional<String> getRequest(final BufferedReader input) throws IOException {
		final long before = System.currentTimeMillis();
		boolean isTechConn = false;
		while (!input.ready()) {
			final long after = System.currentTimeMillis();
			if (after - before > TC_TIME_OUT) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Technical connection handled");
				}
				isTechConn = true;
				break;
			}
		}
		return isTechConn ? Optional.empty() : readRequest(input);
	}

	private Optional<String> readRequest(final BufferedReader input) throws IOException {
		final String requestString = getRequestString(input);
		if (LOG.isDebugEnabled()) {
			LOG.debug(requestString);
		}
		return Optional.of(requestString);
	}

	private String getRequestString(final BufferedReader input) throws IOException {
		final Pattern patternBodyLength = Pattern.compile("Content-Length");
		final Pattern patternHeadersEnd = Pattern.compile("^$");
		return getRequestString(input, patternBodyLength, patternHeadersEnd);
	}

	private String getRequestString(final BufferedReader input, final Pattern patternBodyLength,
									final Pattern patternHeadersEnd) throws IOException {
		final StringBuilder requestString = new StringBuilder();
		int contentLength = 0;
		while (input.ready()) {
			final String currentLine = input.readLine();
			requestString.append(currentLine).append('\n');
			contentLength = getContentLengthIfMatch(patternBodyLength, contentLength, currentLine);
			completeHeadersIfMatch(input, patternHeadersEnd, requestString, contentLength, currentLine);
		}
		return requestString.toString();
	}

	private void completeHeadersIfMatch(final BufferedReader input, final Pattern patternHeadersEnd,
										final StringBuilder requestString, final int contentLength,
										final String currentLine) throws IOException {
		final Matcher matcherHeadersEnd = patternHeadersEnd.matcher(currentLine);
		if (matcherHeadersEnd.find()) {
			requestString.append(requestBodyToString(input, contentLength));
		}
	}

	private int getContentLengthIfMatch(final Pattern patternBodyLength, int contentLength,
										final String currentLine) {
		final Matcher matcherBodyLength = patternBodyLength.matcher(currentLine);
		if (matcherBodyLength.find()) {
			contentLength = Integer.parseInt(currentLine.split(":")[1].trim());
		}
		return contentLength;
	}

	private String requestBodyToString(final BufferedReader input, final int contentLength)
			throws IOException {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < contentLength; i++) {
			final char value = (char) input.read();
			result.append(value);
		}
		return result.toString();
	}
}
