export default ((formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'id',
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/npm`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: 'infra.prod.lib.model.packageName', defaultMessage: '包名称' }) },
  ],
  queryFields: [
    { name: 'name', type: 'string', label: formatMessage({ id: 'infra.prod.lib.model.packageName', defaultMessage: '包名称' }) },
  ],
}));
