import React, { useCallback, useMemo } from 'react';
import { Modal, DataSet } from 'choerodon-ui/pro';
import AssignRepoModal from './AssignRepoModal';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const AssignRepo = ({ formatMessage, name, libListDs, repositoryId, item, repoType }) => {
  const assignDs = React.useRef(new DataSet({
    fields: [
      {
        name: 'name',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.repoName`, defaultMessage: '仓库名称' }),
      },
      {
        name: 'organizationId',
        type: 'number',
        label: formatMessage({ id: `${intlPrefix}.model.organizationId`, defaultMessage: '组织' }),
        // lookupUrl: '/iam/choerodon/v1/organizations?enabledFlag=true',
        // valueField: 'tenantId',
        // textField: 'tenantName',
        required: true,
      },
      {
        name: 'projectId',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.projectId`, defaultMessage: '项目' }),
        valueField: 'id',
        textField: 'name',
        cascadeMap: { organizationId: 'organizationId' },
        dynamicProps: {
          lookupUrl: ({ record }) => `/iam/choerodon/v1/prod/organizations/${record.get('organizationId') || 0}/projects/all`,
        },
        required: true,
      },
      {
        name: 'distributeRepoAdminId',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.distributeRepoAdminId`, defaultMessage: '仓库管理员' }),
        // cascadeMap: { projectId: 'projectId' },
        valueField: 'id',
        textField: 'realName',
        dynamicProps: {
          lookupUrl: ({ record }) => `/iam/choerodon/v1/prod/projects/${record.get('projectId') || 0}/owner/list`,
        },
        required: true,
      },
      {
        name: 'allowAnonymous',
        type: 'number',
        label: formatMessage({ id: `${intlPrefix}.model.allowAnonymous`, defaultMessage: '是否允许匿名访问' }),
        required: true,
        defaultValue: 1,
      },
    ],
  })).current;

  const assignRepoModalProps = useMemo(() => ({
    formatMessage,
    assignDs,
    name,
    libListDs,
    item,
    repoType,
  }), [formatMessage, assignDs, name, libListDs, item, repoType]);

  const openModal = useCallback(() => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.assignRepo`, defaultMessage: '仓库分配' }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-assign-repo-model',
      children: <AssignRepoModal {...assignRepoModalProps} />,
    });
  }, []);

  return (
    <React.Fragment>
      {repositoryId
        ? <span className="header-title">{name}</span>
        : (
          <span className="product-lib-site-management-lib-list-list-card-header-title c7ncd-prolib-clickText" onClick={openModal}>
            {name}
          </span>
        )}
    </React.Fragment>
  );
};

export default AssignRepo;
