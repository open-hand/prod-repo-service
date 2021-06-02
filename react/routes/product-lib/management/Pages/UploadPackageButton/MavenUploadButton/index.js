import React, { useCallback, useMemo } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
import UploadPackageModal from './UploadPackageModal';
import { useUserAuth } from '../../index';
import { intlPrefix } from '../../../index';

const UploadPackageButton = ({
  repositoryId, repositoryName, formatMessage, mavenUploadPackageDs, nexusComponentDs,
}) => {
  const userAuth = useUserAuth();
  const uploadPackageModalProps = useMemo(() => ({
    repositoryId, repositoryName, formatMessage, mavenUploadPackageDs, nexusComponentDs,
  }), [repositoryId, repositoryName, nexusComponentDs, mavenUploadPackageDs, formatMessage]);

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
    <>
      {
        // 项目管理员或开发人员
        userAuth.some((val) => ['projectAdmin', 'developer'].includes(val))
        && (
        <Permission service={['choerodon.code.project.infra.product-lib.ps.project-owner-maven', 'choerodon.code.project.infra.product-lib.ps.project-member-maven']}>
          <Button
            icon="unarchive"
            onClick={openModal}
          >
            {formatMessage({ id: `${intlPrefix}.view.uploadPackage`, defaultMessage: '上传包' })}
          </Button>
        </Permission>
        )
      }
    </>
  );
};

export default UploadPackageButton;
