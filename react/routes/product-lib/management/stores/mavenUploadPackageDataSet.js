export default ((organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  fields: [
    {
      name: 'repositoryName',
      type: 'string',
      label: '仓库',
      lookupUrl: `/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/maven/repo/component`,
      textField: 'name',
      valueField: 'name',
      required: true,
    },
    { name: 'version', type: 'string', label: 'version', required: true, pattern: new RegExp(/^[.A-Za-z0-9_-]+$/) },
    { name: 'groupId', type: 'string', label: 'groupId', required: true, pattern: /^[.A-Za-z0-9_-]+$/ },
    { name: 'artifactId', type: 'string', label: 'artifactId', required: true, pattern: /^[.A-Za-z0-9_-]+$/ },
  ],
}));
