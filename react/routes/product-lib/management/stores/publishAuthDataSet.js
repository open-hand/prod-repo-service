/* eslint-disable */
import React from 'react';
import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-auths/${projectId}/list-project`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'loginName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.loginName`, defaultMessage: '登录名' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }) },
    { name: 'memberRole', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.memberRole`, defaultMessage: '项目成员角色' }) },
    {
      name: 'roleCode',
      type: 'string',
      lookupCode: 'RDUPM.NEXUS_ROLE',
      label:
        <Tips
          title={formatMessage({ id: `${intlPrefix}.model.roleCode`, defaultMessage: '权限角色' })}
          helpText={
            <div>
              权限角色可拥有的权限如下<br />
              1.仓库管理员：push包、pull包、删除包、修改仓库配置(且用户同时是项目所有者)、查看日志<br />
              2.开发人员：push包、pull包、删除包<br />
              3.访客：pull包
            </div>
          }
        />,
    },
    { name: 'endDate', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.endDate01`, defaultMessage: '过期时间' }) },
  ],
  queryFields: [
    { name: 'loginName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.loginName`, defaultMessage: '登录名' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }) },
    {
      name: 'roleCode',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.roleCode`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
    },
  ],
}));
