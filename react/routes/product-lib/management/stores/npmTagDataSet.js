export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {},
  fields: [
    { name: 'version', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
    { name: 'name', type: 'string', label: formatMessage({ id: 'name', defaultMessage: '名称' }) },
    { name: 'sha1', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.hashVale`, defaultMessage: 'hash值' }) },
  ],
  queryFields: [
    { name: 'version', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
  ],
}));
