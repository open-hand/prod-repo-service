export default (({ intlPrefix, formatMessage, organizationId, repositoryName, name, repositoryId }) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/organizations/${organizationId}/npm/version?repositoryName=${repositoryName}&name=${name}&repositoryId=${repositoryId}`,
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'version',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
    {
      name: 'downloadUrl',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.downloadUrl` }),
    },
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
      name: 'version',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
  ],
}));
