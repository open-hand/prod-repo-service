package org.hrds.rdupm.nexus.client.nexus.exception;

import io.choerodon.core.exception.CommonException;
import org.springframework.http.HttpStatus;

/**
 * @author weisen.yang@hand-china.com 2020/3/18
 */
public class NexusResponseException extends CommonException {
	private HttpStatus status;


	public HttpStatus getStatusCode() {
		return this.status;
	}

	public NexusResponseException(HttpStatus status, String code, Object... parameters) {
		super(code, parameters);
		this.status = status;
	}
}
