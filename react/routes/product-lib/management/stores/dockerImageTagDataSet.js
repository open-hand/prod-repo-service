export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-image-tag/list/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'tagName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
    { name: 'sizeDesc', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.sizeDesc`, defaultMessage: '大小' }) },
    { name: 'dockerVersion', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.dockerVersion`, defaultMessage: 'Docker版本' }) },
    { name: 'os', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.OS/ARCH`, defaultMessage: 'OS/ARCH' }) },
    { name: 'architecture', type: 'string' },
    { name: 'digest', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.digest`, defaultMessage: '摘要' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName1`, defaultMessage: '推送者' }) },
    { name: 'createTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.createTime`, defaultMessage: '创建时间' }) },
    { name: 'pushTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pushTime`, defaultMessage: '最新推送时间' }) },
    { name: 'pullTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pullTime`, defaultMessage: '最近拉取时间' }) },
  ],
  queryFields: [
    { name: 'tagName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
  ],
}));
