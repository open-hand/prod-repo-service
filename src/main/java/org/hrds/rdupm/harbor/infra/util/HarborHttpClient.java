package org.hrds.rdupm.harbor.infra.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.harbor.config.DisableSSLCertificateCheck;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * description
 *
 * @author chenxiuhong 2020/04/21 11:46 上午
 */
@Component
public class HarborHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(HarborHttpClient.class);

	String AUTH_HEADER= "Authorization";

	String AUTH_PRE = "Basic ";

	String userName;

	String password;

	@Autowired
	@Qualifier("hrdsHarborRestTemplate")
	private RestTemplate restTemplate;

	@Autowired
	private HarborInfoConfiguration harborInfo;

	public HarborHttpClient buildBasicAuth(String userName,String password){
		this.userName = userName;
		this.password = password;
		return this;
	}

	private String getToken(){
		String basicInfo = this.userName + ":" + this.password;
		return  AUTH_PRE + Base64.getEncoder().encodeToString(basicInfo.getBytes());
	}

	/**
	 * 请求
	 * @param apiEnum api枚举参数
	 * @param paramMap url后面接的参数
	 * @param body body的参数
	 * @param adminAccountFlag 是否使用admin账号认证
	 * @param pathParam 路径参数
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> exchange(HarborConstants.HarborApiEnum apiEnum, Map<String, Object> paramMap, Object body,boolean adminAccountFlag, Object... pathParam){
		String url = harborInfo.getBaseUrl() + apiEnum.getApiUrl();
		paramMap = paramMap == null ? new HashMap<>(2) : paramMap;
		url = this.setParam(url, paramMap,pathParam);
		HttpMethod httpMethod = apiEnum.getHttpMethod();
		if(adminAccountFlag){
			buildBasicAuth(harborInfo.getUsername(),harborInfo.getPassword());
		}else {
			//TODO 使用当前登陆用户账号
			buildBasicAuth("15367","Abcd1234");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(AUTH_HEADER, this.getToken());
		HttpEntity<Object> httpEntity = new HttpEntity<>(new Gson().toJson(body), headers);

		ResponseEntity<String> responseEntity = null;
		try {
			DisableSSLCertificateCheck.disableChecks();
			responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			handleStatusCode(apiEnum,e);
		}finally {
			LOGGER.debug("api：{}",apiEnum);
			LOGGER.debug("url：{}",url);
			LOGGER.debug("body：{}",new Gson().toJson(body));
		}
		return responseEntity;
	}

	/***
	 * url拼接参数：http://api/roject/109?key=value&key=value
	 * @param url
	 * @param paramMap
	 * @return
	 */
	private String setParam(String url, Map<String, Object> paramMap,Object... args){
		url = String.format(url,args);
		if (CollectionUtils.isEmpty(paramMap)) {
			return url;
		}
		StringBuilder newUrl = new StringBuilder().append(url).append(BaseConstants.Symbol.QUESTION);
		for (String key : paramMap.keySet()) {
			if(paramMap.get(key) != null){
				newUrl.append(key).append(BaseConstants.Symbol.EQUAL).append(paramMap.get(key)).append(BaseConstants.Symbol.AND);
			}
		}
		return newUrl.toString().substring(0, newUrl.length() - 1);
	}

	private void handleStatusCode(HarborConstants.HarborApiEnum apiEnum,HttpClientErrorException e){
		int statusCode = e.getStatusCode().value();
		switch (statusCode){
			case 401: throw new CommonException("User need to log in first. or User has no permission to the source project or destination project.");
			case 415: throw new CommonException("The Media Type of the request is not supported, it has to be \"application/json\"");
			case 500: throw new CommonException("Unexpected internal errors.");
			default: break;
		}

		switch (apiEnum){
			case CREATE_PROJECT:
				switch (statusCode){
					case 400: throw new CommonException("Unsatisfied with constraints of the project creation.");
					case 409: throw new CommonException("Project name already exists.");
					default: throw new CommonException(e.getMessage());
				}
			case UPDATE_PROJECT:
				switch (statusCode){
					case 400: throw new CommonException("Illegal format of provided ID value.");
					case 403: throw new CommonException("User does not have permission to the project.");
					case 404: throw new CommonException("Project ID does not exist.");
					default: throw new CommonException(e.getMessage());
				}
			case DETAIL_PROJECT:
				switch (statusCode){
					case 403: throw new CommonException("User does not have permission to the project.");
					case 404: throw new CommonException("Project does not exist.");
					default: throw new CommonException(e.getMessage());
				}
			case GET_PROJECT_SUMMARY:
				switch (statusCode){
					case 400: throw new CommonException("Illegal format of provided ID value.");
					case 403: throw new CommonException("User does not have permission to the project.");
					case 404: throw new CommonException("Project ID does not exist.");
					default: throw new CommonException(e.getMessage());
				}
			case CREATE_USER:
				switch (statusCode){
					case 400: throw new CommonException("Unsatisfied with constraints of the user creation.");
					case 403: throw new CommonException("User registration can only be used by admin role user when self-registration is off.");
					default: throw new CommonException(e.getMessage());
				}
			case COPY_IMAGE_TAG:
				switch (statusCode){
					case 400: throw new CommonException("Invalid image values provided.");
					case 403: throw new CommonException("Forbiden as quota exceeded.");
					case 404: throw new CommonException("Project or repository not found.");
					case 409: throw new CommonException("Target tag already exists.");
					case 412: throw new CommonException("error.harbor.image.tag.copy.412");
					default: throw new CommonException(e.getMessage());
				}
			case CHECK_PROJECT_NAME:
				switch (statusCode){
					case 404: return;
					default: throw new CommonException(e.getMessage());
				}
			case CREATE_ONE_AUTH:
				switch (statusCode){
					case 400: throw new CommonException("Illegal format of project member or project id is invalid, or LDAP DN is invalid.");
					case 403: throw new CommonException("User in session does not have permission to the project.");
					case 409: throw new CommonException("A user group with same group name already exist or an LDAP user group with same DN already exist.");
					default: throw new CommonException(e.getMessage());
				}
			case UPDATE_PROJECT_QUOTA:
				switch (statusCode){
					case 400: throw new CommonException("Illegal format of quota update request.");
					default: throw new CommonException(e.getMessage());
				}
			case DELETE_PROJECT:
				switch (statusCode){
					case 400: throw new CommonException("Invalid project id.");
					case 403: throw new CommonException("User need to log in first.");
					case 404: throw new CommonException("Project does not exist.");
					case 412: throw new CommonException("error.delete.project.412");
					default: throw new CommonException(e.getMessage());
				}
			default: throw new CommonException(e.getMessage());
		}

	}
}
