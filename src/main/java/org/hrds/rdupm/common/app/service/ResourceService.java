package org.hrds.rdupm.common.app.service;

import java.util.List;
import org.hrds.rdupm.nexus.api.vo.ResourceVO;

/**
 * Created by wangxiang on 2021/11/9
 */
public interface ResourceService {
    List<ResourceVO> listResourceByIds(List<Long> projectIds);
}
