const intlPrefix = 'infra.prod.lib';

export default ((formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'id',
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/${organizationId}/project/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
    { name: 'creatorRealName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.author`, defaultMessage: '创建人' }) },
    { name: 'creationDate', type: 'string', label: formatMessage({ id: 'createDate', defaultMessage: '创建时间' }) },
  ],
  queryFields: [
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
  ],
}));
