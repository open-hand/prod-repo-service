export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: '/rdupm/v1/nexus-repositorys/site/repo?repoType=MAVEN',
      method: 'GET',
    }),
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.repoName` }) },
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.type` }) },
    { name: 'versionPolicy', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.versionPolicy` }) },
    { name: 'online', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.online` }) },
    {
      name: 'allowAnonymous',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.allowAnonymous` }),
    },
    { name: 'url', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.url` }) },
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.type` }) },
    { name: 'versionPolicy', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.versionPolicy` }) },
  ],
  queryFields: [
    {
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
    {
      name: 'type',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.type` }),
      lookupCode: 'RDUPM.MAVEN_REPOSITORY_TYPE',
    },
    {
      name: 'versionPolicy',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.versionPolicy` }),
      lookupCode: 'RDUPM.MAVEN_VERSION_POLICY',
    },
    {
      name: 'distributedQueryFlag',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.distributedQueryFlag` }),
      defaultValue: 0,
    },
  ],
}));
