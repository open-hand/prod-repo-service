/* eslint-disable */
import React from 'react';
import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-auths/list-project/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'loginName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.loginName`, defaultMessage: '登录名' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }) },
    { name: 'memberRole', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.memberRole`, defaultMessage: '项目成员角色' }) },
    {
      name: 'harborRoleValue',
      type: 'string',
      // label: formatMessage({ id: `${intlPrefix}.model.harborRoleValue`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
      label:
        <Tips
          title={formatMessage({ id: `${intlPrefix}.model.harborRoleValue`, defaultMessage: '权限角色' })}
          helpText={
            <div>
              权限角色可拥有的权限如下<br />
              1.仓库管理员：pull、push、操作日志。若同时为项目管理员，则可以分配权限。<br />
              2.开发人员：pull、push<br />
              3.访客：pull<br />
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
      name: 'harborRoleValue',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborRoleValue`, defaultMessage: '权限角色' }),
      lookupCode: 'RDUPM.HARBOR_ROLE',
    },
  ],
}));

