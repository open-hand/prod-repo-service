/* eslint-disable */
import React, { useCallback, useMemo } from 'react';
import { Tooltip, Icon } from 'choerodon-ui';
import { useFormatMessage } from "@choerodon/master";
import { Modal, Button } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
import NexusAssociateModal from './NexusAssociateModal';

const intlPrefix = 'infra.prod.lib';

const NexusAssociateBtn = ({ formatMessage, init }) => {
  const nexusAssociateModalProps = useMemo(() => ({ init, formatMessage }), [init, formatMessage]);

  const format = useFormatMessage('c7ncd.productLib');

  const title =
    (
      <span>{formatMessage({ id: `${intlPrefix}.view.customNexus`, defaultMessage: '自定义nexus服务' })}
        <Tooltip title="创建maven、npm制品仓库时，默认使用的是Choerodon平台的nexus服务，也可以自定义添加使用另外的nexus服务，但同一个项目同时只能启用一个nexus服务">
          <Icon type="help" style={{ marginLeft: '5px', marginTop: '-3px', color: 'rgba(0,0,0,0.36)' }} />
        </Tooltip>
      </span>
    );

  const openModal = useCallback(() => {
    const key = Modal.key();
    const modal = Modal.open({
      key,
      title,
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-create-model',
      children: <NexusAssociateModal {...nexusAssociateModalProps} />,
      okCancel: false,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
      onOk: () => modal.close(),
    });
  }, []);

  return (
    <Permission
      service={[
        'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
        'choerodon.code.project.infra.product-lib.ps.project-owner-npm',
      ]}
    >
      <Button
        icon="filter_center_focus"
        onClick={openModal}
        color="primary"
      >
        {format({ id: 'CustomizeService' })}
      </Button>
    </Permission>
  );
};

export default NexusAssociateBtn;
