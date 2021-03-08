package org.hrds.rdupm.harbor.infra.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.hrds.rdupm.common.domain.entity.ProdUser;
import org.hrds.rdupm.common.domain.repository.ProdUserRepository;
import org.hrds.rdupm.harbor.config.DisableSSLCertificateCheck;
import org.hrds.rdupm.harbor.config.HarborCustomConfiguration;
import org.hrds.rdupm.harbor.config.HarborInfoConfiguration;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.util.DESEncryptUtil;
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
	@Autowired
	private ProdUserRepository prodUserRepository;
	@Autowired
	private HarborCustomConfiguration harborCustomConfiguration;

	public HarborHttpClient buildBasicAuth(String userName,String password){
		this.userName = userName;
		this.password = password;
		return this;
	}

	public HarborHttpClient buildCustomBasicAuth(HarborCustomConfiguration harborCustomConfiguration){
		this.harborCustomConfiguration = harborCustomConfiguration;
		return this;
	}

	private String getToken(){
		String basicInfo = this.userName + ":" + this.password;
		return  AUTH_PRE + Base64.getEncoder().encodeToString(basicInfo.getBytes());
	}

	private String getCustomToken(){
		String basicInfo = harborCustomConfiguration.getLoginName() + ":" + harborCustomConfiguration.getPassword();
		return  AUTH_PRE + Base64.getEncoder().encodeToString(basicInfo.getBytes());
	}

	public void setHarborCustomConfiguration(HarborCustomConfiguration harborCustomConfiguration){
		this.harborCustomConfiguration = harborCustomConfiguration;
	}

	public HarborCustomConfiguration getHarborCustomConfiguration() {
		return harborCustomConfiguration;
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
	public ResponseEntity<String> exchange(HarborConstants.HarborApiEnum apiEnum, Map<String, Object> paramMap, Object body, boolean adminAccountFlag, Object... pathParam) {
		String url = HarborUtil.isApiVersion1(harborInfo) ? harborInfo.getBaseUrl() + apiEnum.getApiUrl() : harborInfo.getBaseUrl() + apiEnum.getApiUrlV2();

		paramMap = paramMap == null ? new HashMap<>(2) : paramMap;
		url = this.setParam(url, paramMap,pathParam);
		HttpMethod httpMethod = apiEnum.getHttpMethod();
		String userName = DetailsHelper.getUserDetails() == null ? HarborConstants.ANONYMOUS : DetailsHelper.getUserDetails().getUsername();

		//使用admin账号认证或者当前用户名=admin或者匿名用户创建时，使用当前项目配置的账号连接
		if(adminAccountFlag || HarborConstants.ADMIN.equals(userName) || HarborConstants.ANONYMOUS.equals(userName)){
			buildBasicAuth(harborInfo.getUsername(),harborInfo.getPassword());
		}else {
			ProdUser prodUser = prodUserRepository.select(ProdUser.FIELD_LOGIN_NAME,userName).stream().findFirst().orElse(null);
			String passwd = prodUser == null ? null : (prodUser.getPwdUpdateFlag() == 1 ? DESEncryptUtil.decode(prodUser.getPassword()) : prodUser.getPassword());
			buildBasicAuth(userName,passwd);
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
			int statusCode = e.getStatusCode().value();
			if(statusCode == 404){
				return new ResponseEntity<String>((String) null,HttpStatus.NO_CONTENT);
			}
			handleStatusCode(apiEnum,e);
		}finally {
			LOGGER.debug("api：{}",apiEnum);
			LOGGER.debug("url：{}",url);
			LOGGER.debug("body：{}",new Gson().toJson(body));
		}
		return responseEntity;
	}

	/**
	 * 自定义请求
	 * @param apiEnum api枚举参数
	 * @param paramMap url后面接的参数
	 * @param body body的参数
	 * @param pathParam 路径参数
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> customExchange(HarborConstants.HarborApiEnum apiEnum, Map<String, Object> paramMap, Object body, Object... pathParam) {
		String url = HarborUtil.isApiVersion1(harborCustomConfiguration) ? harborCustomConfiguration.getUrl() + apiEnum.getApiUrl() : harborCustomConfiguration.getUrl() + apiEnum.getApiUrlV2();
		paramMap = paramMap == null ? new HashMap<>(2) : paramMap;
		url = this.setParam(url, paramMap, pathParam);
		HttpMethod httpMethod = apiEnum.getHttpMethod();

		buildCustomBasicAuth(harborCustomConfiguration);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(AUTH_HEADER, this.getCustomToken());
		HttpEntity<Object> httpEntity = new HttpEntity<>(new Gson().toJson(body), headers);

		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			int statusCode = e.getStatusCode().value();
			switch (statusCode){
				case 401: throw new CommonException("error.harbor.custom.repo.current.user");
				case 500: throw new CommonException("error.harbor.custom.repo.url");
				default: break;
			}
			handleStatusCode(apiEnum,e);
		}finally {
			LOGGER.debug("api：{}",apiEnum);
			LOGGER.debug("url：{}",url);
			LOGGER.debug("body：{}",new Gson().toJson(body));
		}
		return responseEntity;
	}



	/**
	 * 获取harbor api版本
	 *
	 * @param apiEnum api枚举参数
	 * @return ResponseEntity<String>
	 */
	public String getSystemInfo(HarborConstants.HarborApiEnum apiEnum, String apiVersion) {
		String url = apiVersion.equals(HarborConstants.API_VERSION_1) ? harborCustomConfiguration.getUrl() + apiEnum.getApiUrl() : harborCustomConfiguration.getUrl() + apiEnum.getApiUrlV2();
		HttpMethod httpMethod = apiEnum.getHttpMethod();
		buildCustomBasicAuth(harborCustomConfiguration);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(AUTH_HEADER, this.getCustomToken());
		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			int statusCode = e.getStatusCode().value();
			if (statusCode == 404 && apiVersion.equals(HarborConstants.API_VERSION_1)) {
				return getSystemInfo(apiEnum, HarborConstants.API_VERSION_2);
			} else {
				throw new CommonException(e.getMessage());
			}
		}
		Map<String, Object> systemMap = JSONObject.parseObject(responseEntity.getBody(), Map.class);
		if (systemMap == null) {
			throw new CommonException("error.get.system.version");
		}
		return systemMap.get("harbor_version").toString().substring(0,2);
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
		for (Map.Entry<String,Object> entry : paramMap.entrySet()) {
			if(entry.getValue() != null){
				newUrl.append(entry.getKey()).append(BaseConstants.Symbol.EQUAL).append(entry.getValue()).append(BaseConstants.Symbol.AND);
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
					case 400: throw new CommonException("error.harbor.api.create.project.name.valid");
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
            case GET_ONE_ROBOT:
                switch (statusCode){
                    case 403: throw new CommonException("User in session does not have permission to the project.");
                    case 404: throw new CommonException("The robot account is not found.");
                    default: throw new CommonException(e.getMessage());
                }
            case DELETE_ROBOT:
                switch (statusCode){
                    case 403: throw new CommonException("User in session does not have permission to the project.");
                    case 404: throw new CommonException("The robot account is not found.");
                    default: throw new CommonException(e.getMessage());
                }
			case CHANGE_PASSWORD:
				switch (statusCode){
					case 400: return;//throw new CommonException("the new password can not be same with the old one.");
					default: throw new CommonException(e.getMessage());
				}
			default: throw new CommonException(e.getMessage());
		}

	}
}
