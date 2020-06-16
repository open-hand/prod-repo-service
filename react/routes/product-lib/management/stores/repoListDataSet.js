export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: () => ({
      url: `/rdupm/v1/product-library/list/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'repositoryName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.repoName` }) },
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.type` }) },
    { name: 'versionPolicy', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.versionPolicy` }) },
    { name: 'online', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.online` }) },
    {
      name: 'allowAnonymous',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.allowAnonymous` }),
    },
    { name: 'url', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.url` }) },
  ],
}));
