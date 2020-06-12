package org.hrds.rdupm.nexus.client.nexus.api.http;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.io.IOUtils;
import org.hrds.rdupm.nexus.client.nexus.NexusRequest;
import org.hrds.rdupm.nexus.client.nexus.api.NexusScriptApi;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusApiConstants;
import org.hrds.rdupm.nexus.client.nexus.constant.NexusUrlConstants;
import org.hrds.rdupm.nexus.client.nexus.exception.NexusResponseException;
import org.hrds.rdupm.nexus.client.nexus.model.NexusScriptResult;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * nexus 脚本相关api
 * @author weisen.yang@hand-china.com 2020/3/20
 */
@Component
public class NexusScriptHttpApi implements NexusScriptApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(NexusScriptHttpApi.class);

	private static final String FILE_SUFFIX = ".groovy";
	private static final String FILE_PATH = "script/nexus/";

	@Autowired
	private NexusRequest nexusRequest;

	@Override
	public NexusServerScript getScript(String scriptName) {
		String getUrl = NexusUrlConstants.Script.GET_SCRIPT_BY_NAME + scriptName;
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = nexusRequest.exchange(getUrl, HttpMethod.GET, null, null);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return null;
			} else {
				throw e;
			}
		}
		String response = responseEntity.getBody();
		return JSON.parseObject(response, NexusServerScript.class);
	}

	@Override
	public void uploadScript(NexusServerScript nexusScript) {
		ResponseEntity<String> responseEntity = nexusRequest.exchange(NexusUrlConstants.Script.UPLOAD_SCRIPT, HttpMethod.POST, null, nexusScript);
	}

	@Override
	public void updateScript(String scriptName, NexusServerScript nexusScript) {
		String url = NexusUrlConstants.Script.UPDATE_SCRIPT.replace("{scriptName}", scriptName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.PUT, null, nexusScript);

	}

	@Override
	public NexusScriptResult runScript(String scriptName, String param) {
		String url = NexusUrlConstants.Script.RUN_SCRIPT.replace("{scriptName}", scriptName);
		ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.POST, null, param, MediaType.TEXT_PLAIN_VALUE);

		String response = responseEntity.getBody();
		return JSON.parseObject(response, NexusScriptResult.class);
	}

	@Override
	public void deleteScript(String scriptName) {
		String url = NexusUrlConstants.Script.DELETE_SCRIPT.replace("{scriptName}", scriptName);
		try {
			ResponseEntity<String> responseEntity = nexusRequest.exchange(url, HttpMethod.DELETE, null, null);
		} catch (NexusResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.warn("nexus script has been deleted");
			} else {
				throw e;
			}
		}
	}

	@Override
	public void initScript() {
		List<String> scriptIdList = new ArrayList<>(NexusApiConstants.ScriptName.SCRIPT_LIST);

		List<NexusServerScript> serverScriptList = new ArrayList<>();
		scriptIdList.forEach(scriptId -> {
			String fileName = scriptId.substring(NexusApiConstants.ScriptName.SCRIPT_PREFIX.length()) + FILE_SUFFIX;

			String filePath = FILE_PATH + fileName;
			try (
					InputStream	is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
			) {
				String content = IOUtils.toString(is, StandardCharsets.UTF_8);
				NexusServerScript nexusServerScript = new NexusServerScript();
				nexusServerScript.setName(scriptId);
				nexusServerScript.setContent(content);
				nexusServerScript.setType(NexusApiConstants.ScriptName.TYPE);
				serverScriptList.add(nexusServerScript);
			} catch (IOException e) {
				LOGGER.error("初始化script脚本获取对应文件失败：" +  filePath, e);
				throw new CommonException(e);
			}
		});

		serverScriptList.forEach(nexusServerScript -> {
			NexusServerScript exist = this.getScript(nexusServerScript.getName());
			if (exist == null) {
				// 新建
				this.uploadScript(nexusServerScript);
			} else {
				// 更新
				exist.setContent(nexusServerScript.getContent());
				this.updateScript(nexusServerScript.getName(), nexusServerScript);
			}
		});
	}
}
