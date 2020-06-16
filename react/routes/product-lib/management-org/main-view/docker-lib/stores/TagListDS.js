export default (({ intlPrefix, formatMessage, repoName, projectId }) => ({
  autoQuery: false,
  selection: false,
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
      name: 'tagName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
    {
      name: 'osVersion',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.osVersion` }),
    },
    {
      name: 'sizeDesc',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.size` }),
    },
    {
      name: 'dockerVersion',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.dockerVersion` }),
    },
    {
      name: 'os',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.os` }),
    },
    { name: 'architecture', type: 'string' },
    {
      name: 'digest',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.digest` }),
    },
    {
      name: 'author',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.realName1` }),
    },
    {
      name: 'createTime',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.createTime` }),
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
