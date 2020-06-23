// import React from 'react';
// import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, organizationId, repoListDs) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-auths/${organizationId}/list-org?repoType=NPM`,
      method: 'GET',
    }),
  },
  fields: [
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
    {
      name: 'loginName',
      type: 'string',
      label: formatMessage({ id: 'loginName' }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: 'userName' }),
    },
    {
      name: 'memberRole',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.memberRole`, defaultMessage: '项目成员角色' }),
    },
    {
      name: 'roleCode',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
      // label: (
      //   <Tips
      //     title={formatMessage({ id: `${intlPrefix}.model.harborRoleValue`, defaultMessage: '权限角色' })}
      //     helpText={
      //       <div>
      //         权限角色可拥有的权限如下<br />
      //         1.项目管理员：push镜像、pull镜像、修改镜像仓库配置、删除镜像、启动扫描器、管理用户权限、查看日志<br />
      //         2.开发人员：push镜像、pull镜像、查看日志<br />
      //         3.访客：pull镜像、查看日志<br />
      //         4.维护人员：push镜像、pull镜像、删除镜像、查看日志<br />
      //         5.受限访客：pull镜像
      //       </div>
      //     }
      //   />
      // ),
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
      label: formatMessage({ id: 'loginName' }),
    },
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: 'userName' }),
    },
    {
      name: 'roleCode',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
      textField: 'meaning',
      valueField: 'meaning',
    },
  ],
}));

