export default ((organizationId, formatMessage, intlPrefix, repoListDs) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'id',
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/organizations/${organizationId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
    {
      name: 'creatorRealName',
      type: 'string',
      label: formatMessage({ id: 'createdByName' }),
    },
    {
      name: 'creationDate',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.createTime` }),
    },
  ],
  queryFields: [
    {
      name: 'repositoryId',
      type: 'string',
      label: formatMessage({ id: 'libName' }),
      textField: 'name',
      valueField: 'repositoryId',
      options: repoListDs,
      required: true,
    },
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
  ],
}));
