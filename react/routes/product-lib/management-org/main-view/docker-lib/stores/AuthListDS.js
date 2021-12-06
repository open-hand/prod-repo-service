import React from 'react';
import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, organizationId, repoListDs, formatClient) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-auths/list-org/${organizationId}`,
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'code',
      type: 'string',
      label: formatClient({ id: 'docker.permission.imageRepositoryCode' }),
    },
    {
      name: 'name',
      type: 'string',
      label: formatClient({ id: 'docker.permission.imageRepositoryName' }),
    },
    {
      name: 'loginName',
      type: 'string',
      label: formatClient({ id: 'docker.permission.loginName' }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatClient({ id: 'docker.permission.userName' }),
    },
    {
      name: 'memberRole',
      type: 'string',
      label: formatClient({ id: 'docker.permission.member' }),
    },
    {
      name: 'harborRoleValue', // TODO
      type: 'string',
      // label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
      label: (
        <Tips
          title={formatClient({ id: 'docker.permission.permissionsToRoles' })}
          helpText={(
            <div>
              权限角色可拥有的权限如下
              <br />
              1.仓库管理员：pull、push、操作日志。若同时为项目管理员，则可以分配权限。
              <br />
              2.开发人员：pull、push
              <br />
              3.访客：pull
              <br />
            </div>
          )}
        />
      ),
    },
    {
      name: 'endDate',
      type: 'string',
      label: formatClient({ id: 'docker.permission.expirationTime' }),
    },
  ],
  queryFields: [
    {
      name: 'code',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoCode` }),
      textField: 'code',
      valueField: 'code',
      options: repoListDs,
      // lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    },
    {
      name: 'name',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
      textField: 'name',
      valueField: 'name',
      options: repoListDs,
      // lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
      // cascadeMap: { parentCodeValueId: 'code' },
    },
    {
      name: 'loginName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.loginName`, defaultMessage: '登录名' }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }),
    },
    {
      name: 'harborRoleName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
      textField: 'meaning',
      valueField: 'meaning',
    },
  ],
}));
