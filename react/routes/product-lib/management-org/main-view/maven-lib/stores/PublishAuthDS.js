import React from 'react';
import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, organizationId, repoListDs) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-auths/${organizationId}/list-org?repoType=MAVEN`,
      method: 'GET',
    }),
  },
  fields: [
    // {
    //   name: 'neUserId',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.neUserId` }),
    // },
    {
      name: 'neRepositoryName',
      type: 'string',
      label: formatMessage({ id: 'libName' }),
    },
    {
      name: 'projectName',
      type: 'string',
      label: formatMessage({ id: 'projectName' }),
    },
    // {
    //   name: 'otherRepositoryName',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.otherRepositoryName` }),
    // },
    // {
    //   name: 'code',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.repoCode` }),
    // },
    // {
    //   name: 'name',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
    // },
    {
      name: 'loginName',
      type: 'string',
      label: formatMessage({
        id: `${intlPrefix}.model.loginName`,
        defaultMessage: '登录名',
      }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }),
    },
    {
      name: 'memberRole',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.memberRole`, defaultMessage: '项目成员角色' }),
    },
    {
      name: 'roleCode', // TODO
      type: 'string',
      // label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.NEXUS_ROLE',
      label: (
        <Tips
          title={formatMessage({ id: `${intlPrefix}.model.harborRoleValue`, defaultMessage: '权限角色' })}
          helpText={
            <div>
              权限角色可拥有的权限如下<br />
              1.仓库管理员：push包、pull包、删除包、修改仓库配置(且用户同时是项目所有者)、管理用户权限(且用户同时是项目所有者)、查看日志<br />
              2.开发人员：push包、pull包、删除包<br />
              3.访客：pull包
            </div>
          }
        />
      ),
    },
    {
      name: 'endDate',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.endDate01`, defaultMessage: '过期时间' }),
    },
  ],
  queryFields: [
    {
      name: 'neRepositoryName',
      type: 'string',
      label: formatMessage({ id: 'libName' }),
      textField: 'name',
      valueField: 'name',
      options: repoListDs,
    },
    {
      name: 'loginName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.loginName` }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.realName` }),
    },
    {
      name: 'roleCode',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborRoleName` }),
      lookupCode: 'RDUPM.NEXUS_ROLE',
    },
  ],
}));
