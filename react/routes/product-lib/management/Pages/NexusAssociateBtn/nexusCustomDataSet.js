import { validateUrl } from '@/utils';

export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {},
  fields: [
    {
      name: 'serverName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.serverName`, defaultMessage: '服务名称' }),
    },
    {
      name: 'serverUrl',
      type: 'string',
      required: true,
      validator: (value) => validateUrl(value),
      label: formatMessage({ id: `${intlPrefix}.model.serverUrl`, defaultMessage: '服务地址' }),
    },
    {
      name: 'userName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.userName`, defaultMessage: '管理用户' }),
      required: true,
    },
    {
      name: 'password',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.password`, defaultMessage: '密码' }),
    },
    {
      name: 'enableAnonymousFlag',
      type: 'number',
      required: true,
      defaultValue: 1,
      label: formatMessage({ id: `${intlPrefix}.model.enableAnonymousFlag`, defaultMessage: '是否启用匿名访问控制' }),
    },

    {
      name: 'anonymous',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.anonymous`, defaultMessage: '匿名用户' }),
      dynamicProps: {
        required: ({ record }) => record.get('enableAnonymousFlag') === 1,
      },
    },
    {
      name: 'anonymousRole',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.anonymousRole`, defaultMessage: '匿名用户对应角色' }),
      dynamicProps: {
        required: ({ record }) => record.get('enableAnonymousFlag') === 1,
      },
    },
  ],
}));
