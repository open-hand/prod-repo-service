/**
* docker配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/28
* @copyright 2020 ® HAND
*/
/* eslint-disable */
import React, { useCallback } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import { axios, stores, Permission } from '@choerodon/boot';
import { intlPrefix } from '../../../index';
import DockerGuideModal from './DockerGuideModal';

const GuideButton = ({ formatMessage, name }) => {
  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(info) {
      this.info = info;
    },
  }));

  const fetchGuide = useCallback(async () => {
    try {
      const { currentMenuType: { projectId } } = stores.AppState;
      const res = await axios.get(`/rdupm/v1/harbor-guide/project/${projectId}`);
      guideInfo.setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, [name]);

  const handleOpenGuideModal = useCallback(async () => {
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
      children: <DockerGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchGuide, guideInfo]);

  return (
    <Permission
      service={[
        'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
        'choerodon.code.project.infra.product-lib.ps.project-member-harbor',
      ]}
    >
      <Button icon="find_in_page-o" onClick={handleOpenGuideModal}>
        {formatMessage({ id: `${intlPrefix}.view.configGuideTitle`, defaultMessage: '配置指引' })}
      </Button>
    </Permission>
  );
};

export default observer(GuideButton);
