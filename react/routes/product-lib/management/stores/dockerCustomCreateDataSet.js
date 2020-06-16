export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {},
  fields: [
    {
      name: 'repoName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.repoName`, defaultMessage: '仓库名称' }),
    },
    {
      name: 'repoUrl',
      type: 'string',
      required: true,
      pattern: /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/,
      label: formatMessage({ id: `${intlPrefix}.model.repoUrl`, defaultMessage: '仓库地址' }),
    },
    {
      name: 'description',
      type: 'string',
      label: formatMessage({ id: 'description', defaultMessage: '描述' }),
    },
    {
      name: 'loginName',
      type: 'string',
      required: true,
      label: formatMessage({ id: 'loginName', defaultMessage: '登录名' }),
    },
    {
      name: 'password',
      type: 'string',
      required: true,
      label: formatMessage({ id: 'password', defaultMessage: '密码' }),
    },
    {
      name: 'email',
      type: 'string',
      required: true,
      label: formatMessage({ id: 'mailbox', defaultMessage: '邮箱' }),
    },
    {
      name: 'projectShare',
      type: 'string',
      required: true,
      defaultValue: 'false',
      label: formatMessage({ id: `${intlPrefix}.model.projectShare`, defaultMessage: '是否共享' }),
    },
  ],
}));
