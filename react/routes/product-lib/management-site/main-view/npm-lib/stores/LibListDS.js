export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: '/rdupm/v1/nexus-repositorys/site/repo?repoType=NPM',
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'name',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
    {
      name: 'projectName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
    {
      name: 'projectImgUrl',
      type: 'string',
    },
    {
      name: 'creatorRealName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.createdBy` }),
    },
    {
      name: 'createdBy',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.createdBy` }),
    },
    {
      name: 'creatorLoginName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.createdBy` }),
    },
    {
      name: 'creationDate',
      type: 'data',
      label: formatMessage({ id: `${intlPrefix}.model.creationDate` }),
    },
    {
      name: 'repoCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.repoCount` }),
    },
    {
      name: 'publicFlag',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.publicFlag` }),
    },
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
