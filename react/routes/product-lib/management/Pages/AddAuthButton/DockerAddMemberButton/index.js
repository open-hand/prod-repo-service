/* eslint-disable */
import React, { useCallback, useMemo } from 'react';
import { Modal, Button } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
import AddMemberModal from './AddMemberModal';
import { useUserAuth } from '../../index';

const intlPrefix = 'infra.prod.lib';

const AddMemberButton = ({ formatMessage, dockerAuthDs }, ...props) => {
  const userAuth = useUserAuth();
  const addMemberModalProps = useMemo(() => ({ formatMessage, dockerAuthDs }), [dockerAuthDs, formatMessage]);

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
    <>
      {userAuth?.includes('projectAdmin')
        && (
        <Permission service={['choerodon.code.project.infra.product-lib.ps.project-owner-harbor']}>
          <Button
            icon="playlist_add"
            onClick={openModal}
            color="primary"
            {...props}
          >
            {formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' })}
          </Button>
        </Permission>
        )}
    </>
  );
};

export default AddMemberButton;
