/*eslint-disable*/
import React, { useCallback, useMemo } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
import AddMemberModal from './AddMemberModal';
import { useUserAuth, useAuthPermisson } from '../../index';

const intlPrefix = 'infra.prod.lib';

const AddMemberButton = ({ repositoryId, formatMessage, publishAuthDs, activeRepository }, ...props) => {
  const userAuth = useUserAuth();

  const addMemberModalProps = useMemo(() => ({ repositoryId, formatMessage, publishAuthDs }), [repositoryId, publishAuthDs, formatMessage]);

  const openModal = useCallback(() => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      style: { width: '740px' },
      children: <AddMemberModal {...addMemberModalProps} />,
    });
  }, []);

  const getDom = () => {
    if (['MAVEN', 'NPM'].includes(activeRepository.productType)) {
      const useAuthPermission = useAuthPermisson();
      if (useAuthPermission[activeRepository.productType][activeRepository.repositoryId]?.includes('projectAdmin')){
        return <Permission
          service={[
            'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
            'choerodon.code.project.infra.product-lib.ps.project-owner-npm',
          ]}
        >
          <Button
            icon="playlist_add"
            onClick={openModal}
            color="primary"
            {...props}
          >
            {formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' })}
          </Button>
        </Permission>
      }
    } else {
      return userAuth.includes('projectAdmin') &&
      <Permission
        service={[
          'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
          'choerodon.code.project.infra.product-lib.ps.project-owner-npm',
        ]}
      >
        <Button
          icon="playlist_add"
          onClick={openModal}
          color="primary"
          {...props}
        >
          {formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' })}
        </Button>
      </Permission>
    }
  }

  return (
    <React.Fragment>
      {getDom()}
    </React.Fragment>
  );
};

export default AddMemberButton;
