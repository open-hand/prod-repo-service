/* eslint-disable */
/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { reaction } from 'mobx';
import { observer, useLocalStore } from 'mobx-react-lite';
import { Permission } from '@choerodon/boot';
import { intlPrefix } from '../../../index';
import NpmGuideModal from './NpmGuideModal';


const GuideButton = ({ formatMessage, npmOverViewDs }) => {
  const guideInfo = useLocalStore(() => ({
    info: {
      setRegistory: 'npm config set registry=',
      login: 'npm login',
      pull: 'npm install testDemo@1.1.0 --registry=',
      packagejson: JSON.stringify({
        name: 'testDemo',
        version: '1.1.0',
        description: '',
        main: 'index.js',
        author: '',
        license: 'MIT',
      }),
      pushcmd: 'npm publish --registry=http://test',
    },
    setGuideInfo(info) {
      this.info = info;
    },
  }));

  const setGuideInfo = (url) => {
    guideInfo.setGuideInfo({
      setRegistory: `npm config set registry=${url}`,
      login: 'npm login',
      pull: `npm install testDemo@1.1.0 --registry=${url}`,
      packagejson: '{\n    "name": testDemo,\n    "version": 1.1.0,\n    "description": "",\n    "main": index.js,\n    "author": "",\n    "license": MIT\n }',
      pushcmd: `npm publish --registry=${url}`,
    });
  };

  React.useEffect(
    () => reaction(() => npmOverViewDs.current && npmOverViewDs.current.get('url'), setGuideInfo),
    [],
  );

  const handleOpenGrantAuthModal = useCallback(async () => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.configGuideTitle`, defaultMessage: '配置指引' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <NpmGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [guideInfo]);

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
