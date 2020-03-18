package org.hrds.rdupm.nexus.client.nexus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author weisen.yang@hand-china.com 2020/3/17
 */
public class NexusCustomErrorHandler implements ResponseErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NexusCustomErrorHandler.class);

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return true;
	}
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		LOGGER.error("nexus api status info : {}", response.getStatusCode());
		LOGGER.error("nexus api error info : {}", response.getStatusText());
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
