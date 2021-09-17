export default ((intlPrefix, formatMessage, organizationId, userId, repoListDs) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-repositorys/organizations/${organizationId}/npm/repo`,
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
      name: 'projectId',
      type: 'string',
      label: formatMessage({ id: 'infra.codelib.audit.model.projectName' }),
      lookupUrl: `/iam/choerodon/v1/organizations/${organizationId}/users/${userId}/projects`,
      textField: 'name',
      valueField: 'id',
    },
    {
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
      textField: 'name',
      valueField: 'name',
      options: repoListDs,
    },
    {
      name: 'type',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.type` }),
      lookupCode: 'RDUPM.MAVEN_REPOSITORY_TYPE',
    },
  ],
}));
