export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: ({ dataSet }) => ({
      url: `/rdupm/v1/harbor-image/list-project/${dataSet.projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'imageName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.imageName`, defaultMessage: '镜像名称' }) },
    { name: 'updateTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.updateTime`, defaultMessage: '最新更新时间' }) },
    { name: 'tagsCount', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagsCount`, defaultMessage: '版本数' }) },
    { name: 'pullCount', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pullCount`, defaultMessage: '下载数' }) },
  ],
  queryFields: [
    { name: 'imageName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.imageName`, defaultMessage: '镜像名称' }) },
  ],
}));
