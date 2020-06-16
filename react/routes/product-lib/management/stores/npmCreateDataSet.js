export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {},
  fields: [
    {
      name: 'type',
      type: 'string',
      lookupCode: 'RDUPM.MAVEN_REPOSITORY_TYPE',
      label: formatMessage({ id: `${intlPrefix}.model.type` }),
    },
    {
      name: 'name',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
      required: true,
      validator: (value) => {
        // eslint-disable-next-line
        if (/[^\w\d\_\-\.]/.test(value) || /\.$/.test(value)) {
          return '请输入合法名称，仅允许英文、数字、下划线、中划线、点(.)';
        }
        return true;
      },
    },
    {
      name: 'writePolicy',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.writePolicy` }),
      lookupCode: 'RDUPM.MAVEN_WRITE_POLICY',
      dynamicProps: {
        required: ({ record }) => record.get('type') === 'hosted',
      },
    },
    {
      name: 'allowAnonymous',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.allowAnonymous` }),
      defaultValue: 1,
    },
    {
      name: 'remoteUrl',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.remoteUrl` }),
      validator: (value, _, record) => {
        if (!new RegExp('^(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$').test(value) && record.get('type') === 'proxy') {
          return '地址填写有误，如：http://www.example.com';
        }
        return true;
      },
      dynamicProps: {
        required: ({ record }) => record.get('type') === 'proxy',
      },
    },
    {
      name: 'remoteUsername',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.remoteUsername` }),
    },
    {
      name: 'remotePassword',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.remotePassword` }),
    },
  ],
  queryFields: [],
}));
