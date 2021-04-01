import { axios } from '@choerodon/boot';

export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-image-tag/list/${projectId}`,
      method: 'GET',
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
    { name: 'severity', type: 'string', label: '安全扫描结果' },
    { name: 'scanStatus', type: 'string', label: '扫描状态' },
    { name: 'sizeDesc', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.sizeDesc`, defaultMessage: '大小' }) },
    { name: 'os', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.OS/ARCH`, defaultMessage: 'OS/ARCH' }) },
    { name: 'digest', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.digest`, defaultMessage: '摘要' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName1`, defaultMessage: '推送者' }) },
    { name: 'pushTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pushTime`, defaultMessage: '最近推送时间' }) },
    { name: 'pullTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pullTime`, defaultMessage: '最近拉取时间' }) },
  ],
  queryFields: [
    { name: 'tagName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
  ],
}));
