import { axios } from '@choerodon/boot';

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
  events: {
    load: async ({ dataSet }) => {
      try {
        const res = await axios.get(`/rdupm/v1/harbor-image/scanner-status?projectId=${projectId}`);
        if (res && res?.failed) {
          return false;
        }
        if (!res) {
          dataSet.forEach((record) => {
            record.selectable = false;
          });
        }
      } catch (error) {
        throw new Error(error);
      }
    },
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
