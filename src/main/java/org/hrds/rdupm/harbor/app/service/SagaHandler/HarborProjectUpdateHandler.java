package org.hrds.rdupm.harbor.app.service.SagaHandler;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.app.service.HarborProjectService;
import org.hrds.rdupm.harbor.domain.entity.HarborProjectDTO;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author chenxiuhong 2020/04/26 5:16 下午
 */
@Component
public class HarborProjectUpdateHandler {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HarborProjectService harborProjectService;

	@Autowired
	private HarborHttpClient harborHttpClient;

	@SagaTask(code = HarborConstants.HarborSagaCode.UPDATE_PROJECT_REPO,description = "更新Docker镜像仓库：保存仓库元数据",
			sagaCode = HarborConstants.HarborSagaCode.UPDATE_PROJECT,seq = 1,maxRetryCount = 3)
	private HarborProjectVo updateProjectRepoSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		HarborProjectDTO harborProjectDTO = new HarborProjectDTO(harborProjectVo);
		harborHttpClient.exchange(HarborConstants.HarborApiEnum.UPDATE_PROJECT,null,harborProjectDTO,false,harborProjectVo.getHarborId());
		return harborProjectVo;
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.UPDATE_PROJECT_QUOTA,description = "更新Docker镜像仓库：保存存储容量配置",
			sagaCode = HarborConstants.HarborSagaCode.UPDATE_PROJECT,seq = 2,maxRetryCount = 3)
	private void updateProjectQuotaSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		harborProjectService.saveQuota(harborProjectVo,harborProjectVo.getHarborId());
	}

	@SagaTask(code = HarborConstants.HarborSagaCode.UPDATE_PROJECT_CVE,description = "更新Docker镜像仓库：保存cve白名单",
			sagaCode = HarborConstants.HarborSagaCode.UPDATE_PROJECT,seq = 2,maxRetryCount = 3)
	private void updateProjectCveSaga(String message){
		HarborProjectVo harborProjectVo = null;
		try {
			harborProjectVo = objectMapper.readValue(message, HarborProjectVo.class);
		} catch (IOException e) {
			throw new CommonException(e);
		}
		harborProjectService.saveWhiteList(harborProjectVo,harborProjectVo.getHarborId());
	}

}
