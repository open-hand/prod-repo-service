/* eslint-disable */
/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import { axios, Permission } from '@choerodon/boot';
import { intlPrefix } from '../../../index';
import MavenGuideModal from './MavenGuideModal';

const GuideButton = ({ repositoryId, formatMessage, name }) => {
  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(info) {
      this.info = info;
    },
  }));

  const fetchGuide = useCallback(async () => {
    try {
      const res = await axios.get(`/rdupm/v1/nexus-repositorys/maven/repo/guide/${name}?repositoryId=${repositoryId}&&showPushFlag=true`);
      guideInfo.setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, [name]);

  const handleOpenGrantAuthModal = useCallback(async () => {
    fetchGuide();
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.configGuideTitle`, defaultMessage: '配置指引' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <MavenGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchGuide, guideInfo]);

  return (
    <Permission
      service={[
        'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
        'choerodon.code.project.infra.product-lib.ps.project-member-maven',
      ]}
    >
      <Button icon="find_in_page-o" onClick={handleOpenGrantAuthModal}>
        {formatMessage({ id: `${intlPrefix}.view.configGuideTitle`, defaultMessage: '配置指引' })}
      </Button>
    </Permission>
  );
};

export default observer(GuideButton);
