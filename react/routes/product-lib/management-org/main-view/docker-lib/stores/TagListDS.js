export default (({ intlPrefix, formatMessage, repoName, projectId }) => ({
  autoQuery: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-image-tag/list/${projectId}?repoName=${repoName}`,
      method: 'GET',
    }),
    destroy: ({ data: [data] }) => ({
      url: `/rdupm/v1/harbor-image-tag/delete?tagName=${data.tagName}&repoName=${repoName}`,
      method: 'delete',
      data,
    }),
  },
  fields: [
    {
      name: 'osVersion',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
    { name: 'severity', type: 'string', label: '扫描状态' },
    { name: 'scanStatus', type: 'string', label: '安全扫描结果' },
    {
      name: 'sizeDesc',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.size` }),
    },
    {
      name: 'os',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.os` }),
    },
    {
      name: 'digest',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.digest` }),
    },
    {
      name: 'pushTime',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.pushTime` }),
    },
    {
      name: 'pullTime',
      type: 'data',
      label: formatMessage({ id: `${intlPrefix}.model.pullTime` }),
    },
  ],
  queryFields: [
    {
      name: 'tagName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
  ],
}));
