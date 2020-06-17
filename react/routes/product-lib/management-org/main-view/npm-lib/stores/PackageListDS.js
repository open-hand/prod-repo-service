export default ((intlPrefix, formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/organizations/${organizationId}/npm`,
      method: 'GET',
    }),
    // destroy: ({ data: [data] }) => ({
    //   url: '/rdupm/v1/harbor-image/delete',
    //   method: 'delete',
    //   data,
    // }),
  },
  fields: [
    {
      name: 'repository',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
    {
      name: 'name',
      type: 'string',
    },
    // {
    //   name: 'updateTime',
    //   type: 'dataTime',
    //   label: formatMessage({ id: `${intlPrefix}.model.updateTime` }),
    // },
    {
      name: 'newestVersion',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.newestVersion` }),
    },
    {
      name: 'versionCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.tagsCount` }),
    },
  ],
  queryFields: [
    {
      name: 'repositoryId',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
      required: true,
      textField: 'name',
      valueField: 'repositoryId',
      lookupUrl: `/rdupm/v1/nexus-repositorys/organizations/${organizationId}/npm/repo/name`,
    },
    {
      name: 'name',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.packageName` }),
    },
  ],
}));
