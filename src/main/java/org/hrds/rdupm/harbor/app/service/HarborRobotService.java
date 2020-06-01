package org.hrds.rdupm.harbor.app.service;

import java.util.List;

import org.hrds.rdupm.harbor.api.vo.HarborRobotVO;
import org.hrds.rdupm.harbor.domain.entity.HarborRepository;
import org.hrds.rdupm.harbor.domain.entity.HarborRobot;

/**
 * 制品库-harbor机器人账户表应用服务
 *
 * @author mofei.li@hand-china.com 2020-05-28 15:29:06
 */
public interface HarborRobotService {
    /**
     * 创建机器人账户
     *
     * @param harborRobot 机器人账户参数
     * @return HarborRobot
     */
    HarborRobot createRobot(HarborRobot harborRobot);

    /**
     * 根据仓库生成机器人账户
     *
     * @param message sage创建仓库序列化消息
     * @return HarborRobot
     */
    String generateRobot(String message);

    /**
     * 根据项目ID查找机器人账户
     *
     * @param projectId 猪齿鱼项目ID
     * @param action 机器人账户功能，push/pull
     * @return List<HarborRobot>
     */
    List<HarborRobot> getRobotByProjectId(Long projectId, String action);

}
