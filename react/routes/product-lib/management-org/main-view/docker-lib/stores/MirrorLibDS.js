export default ((intlPrefix, formatMessage, organizationId, repoListDs, formatClient) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-project/list-org/${organizationId}`,
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'code',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
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
      name: 'code',
      type: 'string',
      label: formatClient({ id: 'docker.mirrorWarehouse.imageRepositoryName' }),
      textField: 'name',
      valueField: 'code',
      options: repoListDs,
      // lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    },
    {
      name: 'publicFlag',
      type: 'string',
      label: formatClient({ id: 'docker.mirrorWarehouse.accessLevel' }),
    },
  ],
}));
