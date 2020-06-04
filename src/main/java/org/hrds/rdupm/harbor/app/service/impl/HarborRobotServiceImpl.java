package org.hrds.rdupm.harbor.app.service.impl;

import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.api.vo.HarborRobotAccessVO;
import org.hrds.rdupm.harbor.api.vo.HarborRobotVO;
import org.hrds.rdupm.harbor.app.service.HarborRobotService;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.HarborRobot;
import org.hrds.rdupm.harbor.domain.repository.HarborRepositoryRepository;
import org.hrds.rdupm.harbor.domain.repository.HarborRobotRepository;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hrds.rdupm.harbor.infra.util.HarborHttpClient;
import org.hrds.rdupm.harbor.infra.util.HarborUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
/**
 * 制品库-harbor机器人账户表应用服务默认实现
 *
 * @author mofei.li@hand-china.com 2020-05-28 15:29:06
 */
@Service
public class HarborRobotServiceImpl implements HarborRobotService {
    public static final Logger logger = LoggerFactory.getLogger(HarborRobotServiceImpl.class);
    @Autowired
    private HarborHttpClient harborHttpClient;
    @Autowired
    private HarborRobotRepository harborRobotRepository;
    @Autowired
    private HarborRepositoryRepository harborRepositoryRepository;

    @Override
    public HarborRobot createRobot(HarborRobot harborRobot) {
        checkRobotParam(harborRobot);
        String robotResource = String.format(HarborConstants.HarborRobot.ROBOT_RESOURCE,harborRobot.getHarborProjectId());
        List<HarborRobotAccessVO> accessVOList = new ArrayList<>(1);
        accessVOList.add(new HarborRobotAccessVO(harborRobot.getAction(), robotResource));

        HarborRobotVO createRobotVo  = new HarborRobotVO(harborRobot.getName(), harborRobot.getDescription(), accessVOList);
        ResponseEntity<String> robotResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.CREATE_ROBOT,null,createRobotVo,false, harborRobot.getHarborProjectId());
        HarborRobotVO newRobotVO = new Gson().fromJson(robotResponseEntity.getBody(), HarborRobotVO.class);
        AssertUtils.notNull(newRobotVO.getToken(),"the robot response token empty");
        AssertUtils.notNull(newRobotVO.getName(),"the robot response name empty");
        harborRobot.setName(newRobotVO.getName());
        harborRobot.setToken(newRobotVO.getToken());

        //查找所有harbor
        ResponseEntity<String> allRobotResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_PROJECT_ALL_ROBOTS,null,null,false,harborRobot.getHarborProjectId());
        List<HarborRobotVO> allRobotVOList = new ArrayList<>();
        if ( null != allRobotResponseEntity && StringUtils.isNotBlank(allRobotResponseEntity.getBody())) {
            allRobotVOList = new Gson().fromJson(allRobotResponseEntity.getBody(),new TypeToken<List<HarborRobotVO>>(){}.getType());
        }
        HarborRobotVO robotInfo = allRobotVOList.stream().filter(x->x.getName().equals(harborRobot.getName())).collect(Collectors.toList()).get(0);
        harborRobot.setEnableFlag(robotInfo.getDisabled() ? HarborConstants.HarborRobot.ENABLE_FLAG_N : HarborConstants.HarborRobot.ENABLE_FLAG_Y);
        harborRobot.setHarborRobotId(robotInfo.getId());
        //设置过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(robotInfo.getExpiresAt() * 1000);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(BaseConstants.Pattern.DATETIME);
        format.format(date);
        try {
            date = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        harborRobot.setEndDate(date);
        harborRobotRepository.insertSelective(harborRobot);
        return harborRobot;
    }

    @Override
    @SagaTask(code = HarborConstants.HarborRobot.ROBOT_SAGA_TASK_CODE, description = "创建harbor机器人账户",
            sagaCode = HarborConstants.HarborSagaCode.CREATE_PROJECT, seq = 4, maxRetryCount = 3, outputSchemaClass = String.class)
    public String generateRobot(String message){
        HarborProjectVo projectVo = new Gson().fromJson(message, HarborProjectVo.class);

        List<HarborRepository> repositoryList = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectVo.getProjectDTO().getId()))
                .andWhere(Sqls.custom().andEqualTo(HarborRepository.FIELD_ORGANIZATION_ID, projectVo.getProjectDTO().getOrganizationId()))
                .build());
        if (CollectionUtils.isEmpty(repositoryList)) {
            throw new CommonException("error.harbor.robot.repository.select");
        }
        HarborRepository repository = repositoryList.get(0);
        List<HarborRobot> harborRobotList = new ArrayList<>(2);

        HarborRobot harborRobot = new HarborRobot();
        harborRobot.setProjectId(repository.getProjectId());
        harborRobot.setHarborProjectId(repository.getHarborId());
        harborRobot.setOrganizationId(repository.getOrganizationId());
        //创建pull账户
        harborRobot.setName(repository.getCode() + BaseConstants.Symbol.MIDDLE_LINE + HarborConstants.HarborRobot.ACTION_PULL);
        harborRobot.setAction(HarborConstants.HarborRobot.ACTION_PULL);
        harborRobot.setDescription(repository.getCode() + BaseConstants.Symbol.SPACE  + HarborConstants.HarborRobot.ACTION_PULL + BaseConstants.Symbol.SPACE + HarborConstants.HarborRobot.ROBOT);
        harborRobotList.add(this.createRobot(harborRobot));

        //创建push账户
        HarborUtil.resetDomain(harborRobot);
        harborRobot.setName(repository.getCode() + BaseConstants.Symbol.MIDDLE_LINE + HarborConstants.HarborRobot.ACTION_PUSH);
        harborRobot.setAction(HarborConstants.HarborRobot.ACTION_PUSH);
        harborRobot.setDescription(repository.getCode() + BaseConstants.Symbol.SPACE  + HarborConstants.HarborRobot.ACTION_PUSH + BaseConstants.Symbol.SPACE + HarborConstants.HarborRobot.ROBOT);
        harborRobotList.add(this.createRobot(harborRobot));
        return new Gson().toJson(harborRobotList);
    }

    private void checkRobotParam(HarborRobot harborRobot) {
        if (!StringUtils.equalsAny(harborRobot.getAction(), HarborConstants.HarborRobot.ACTION_PULL, HarborConstants.HarborRobot.ACTION_PUSH)) {
            throw new CommonException("error.harbor.robot.action.wrong");
        }
        if (StringUtils.isBlank(harborRobot.getName())) {
            throw new CommonException("error.harbor.robot.name.empty", harborRobot.getProjectId());
        }
        if (harborRobot.getHarborProjectId() == null) {
            throw new CommonException("error.harbor.robot.projectId.empty", harborRobot.getProjectId());
        }

    }

    @Override
    public List<HarborRobot> getRobotByProjectId(Long projectId, String action) {
        //校验DB镜像仓库
        List<HarborRepository> repositoryList = harborRepositoryRepository.selectByCondition(Condition.builder(HarborRepository.class)
                .andWhere(Sqls.custom().andEqualTo(HarborRepository.FIELD_PROJECT_ID, projectId))
                .build());
        if (CollectionUtils.isEmpty(repositoryList)) {
            throw new CommonException("error.harbor.robot.repository.select", projectId);
        }
        //查询DB机器人账户
        Date date = new Date();
        List<HarborRobot> harborRobotList;
        if (StringUtils.isEmpty(action)) {
            harborRobotList = harborRobotRepository.selectByCondition(Condition.builder(HarborRobot.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(HarborRobot.FIELD_PROJECT_ID, projectId)
                            .andGreaterThan(HarborRobot.FIELD_END_DATE, date))
                    .build());
        } else if (StringUtils.equalsAny(action, HarborConstants.HarborRobot.ACTION_PULL, HarborConstants.HarborRobot.ACTION_PUSH)) {
            harborRobotList = harborRobotRepository.selectByCondition(Condition.builder(HarborRobot.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(HarborRobot.FIELD_PROJECT_ID, projectId)
                            .andEqualTo(HarborRobot.FIELD_ACTION, action)
                            .andGreaterThan(HarborRobot.FIELD_END_DATE, date))
                    .build());
        } else {
            throw new CommonException("error.harbor.robot.action.wrong");
        }
        //校验Harbor机器人账户
        if (CollectionUtils.isNotEmpty(harborRobotList)) {
            for (HarborRobot robot:harborRobotList
            ) {
                ResponseEntity<String> allRobotResponseEntity = harborHttpClient.exchange(HarborConstants.HarborApiEnum.GET_ONE_ROBOT,null,null,false, repositoryList.get(0).getHarborId(), robot.getHarborRobotId());
                HarborRobotVO harborRobotVO = new Gson().fromJson(allRobotResponseEntity.getBody(), HarborRobotVO.class);
                checkRobotInfo(robot, harborRobotVO);
            }
        }
        return harborRobotList;
    }

    private void checkRobotInfo(HarborRobot robot, HarborRobotVO robotVO) {
        if (!robot.getName().equals(robotVO.getName())) {
            throw new CommonException("error.harbor.robot.name.different");
        }
        if (robotVO.getDisabled() && robot.getEnableFlag().equals(HarborConstants.HarborRobot.ENABLE_FLAG_Y)) {
            throw new CommonException("error.harbor.robot.enabled.different");
        }
        if (robotVO.getExpiresAt() * 1000 != robot.getEndDate().getTime()) {
            throw new CommonException("error.harbor.robot.endDate.different");
        }
    }
}
