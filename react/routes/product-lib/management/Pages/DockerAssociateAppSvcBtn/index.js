/**
* harbor关联应用服务
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/6/09
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Modal } from 'choerodon-ui/pro';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import AssociateModal from './AssociateModal';

const intlPrefix = 'infra.prod.lib';

const DockerAssociateAppSvcBtn = ({ refresh, formatMessage, repositoryId }) => {
  const handleOpenAssociateModal = useCallback(() => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.associatedAppSVC`, defaultMessage: '关联应用服务' }),
      maskClosable: true,
      destroyOnClose: true,
      drawer: true,
      style: { width: '380px' },
      children: <AssociateModal refresh={refresh} repositoryId={repositoryId} />,
      okText: formatMessage({ id: 'associate', defaultMessage: '关联' }),
    });
  }, []);

  return (
    <Button
      onClick={handleOpenAssociateModal}
      icon="library_add-o"
    >
      {formatMessage({ id: `${intlPrefix}.view.associatedAppSVC`, defaultMessage: '关联应用服务' })}
    </Button>
  );
};

export default observer(DockerAssociateAppSvcBtn);
