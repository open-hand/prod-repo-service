import React, { useCallback, useMemo } from 'react';
import { Button } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
import AddMemberModal from './AddMemberModal';
import { useUserAuth } from '../../index';

const intlPrefix = 'infra.prod.lib';

const AddMemberButton = ({ repositoryId, formatMessage, publishAuthDs }) => {
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

  return (
    <React.Fragment>
      {userAuth.includes('projectAdmin') &&
        <Permission
          service={[
            'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
            'choerodon.code.project.infra.product-lib.ps.project-owner-npm',
          ]}
        >
          <Button
            icon="playlist_add"
            onClick={openModal}
          >
            {formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' })}
          </Button>
        </Permission>
      }
    </React.Fragment>
  );
};

export default AddMemberButton;
