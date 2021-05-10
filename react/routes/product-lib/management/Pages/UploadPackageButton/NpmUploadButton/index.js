/* eslint-disable */
import React, { useCallback, useMemo, useRef } from 'react';
import { Button } from 'choerodon-ui';
import { Modal, DataSet } from 'choerodon-ui/pro';
import { Permission, stores } from '@choerodon/boot';
import UploadPackageModal from './UploadPackageModal';
import { useUserAuth } from '../../index';
import { intlPrefix } from '../../../index';

const UploadPackageButton = ({ repositoryId, repositoryName, formatMessage, npmComponentDs }) => {
  const userAuth = useUserAuth();
  const { currentMenuType: { organizationId, projectId } } = stores.AppState;
  const npmUploadPackageDs = useRef(new DataSet({
    autoCreate: true,
    fields: [
      {
        name: 'repositoryName',
        type: 'string',
        label: '仓库',
        required: true,
      },
    ],
  })).current;

  const uploadPackageModalProps = useMemo(() => ({ repositoryId, repositoryName, formatMessage, npmUploadPackageDs, npmComponentDs }), [repositoryId, repositoryName, npmComponentDs, npmUploadPackageDs, formatMessage]);

  const openModal = useCallback(() => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.uploadPackage`, defaultMessage: '上传包' }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      style: { width: '380px' },
      children: <UploadPackageModal {...uploadPackageModalProps} />,
    });
  }, []);

  return (
    <React.Fragment>
      {
        // 项目管理员或开发人员
        userAuth.some(val => ['projectAdmin', 'developer'].includes(val)) &&
        <Permission service={['choerodon.code.project.infra.product-lib.ps.project-owner-maven', 'choerodon.code.project.infra.product-lib.ps.project-member-maven']}>
          <Button
            icon="unarchive"
            onClick={openModal}
          >
            {formatMessage({ id: `${intlPrefix}.view.uploadPackage`, defaultMessage: '上传包' })}
          </Button>
        </Permission>
      }
    </React.Fragment>
  );
};

export default UploadPackageButton;
