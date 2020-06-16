export default ((intlPrefix, formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: ({ dataSet }) => ({
      url: `/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/npm/repo/${dataSet.repositoryId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.repoName` }) },
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.type` }) },
    {
      name: 'writePolicy',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.writePolicy` }),
      lookupCode: 'RDUPM.MAVEN_WRITE_POLICY',
    },
    { name: 'versionPolicy', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.versionPolicy` }) },
    { name: 'online', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.online` }) },
    {
      name: 'allowAnonymous',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.allowAnonymous` }),
    },
    { name: 'url', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.url` }) },
    { name: 'remoteUrl', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.remoteUrl` }) },
    { name: 'repoMemberList', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.repoMemberList` }) },
  ],
}));
