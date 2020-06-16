export default ((intlPrefix, formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/${organizationId}/harbor-custom-repos/relate-service/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.serviceName`, defaultMessage: '服务名称' }) },
    { name: 'code', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.code`, defaultMessage: '服务编码' }) },
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.svcType`, defaultMessage: '服务类型' }) },
    { name: 'creationDate', type: 'string', label: formatMessage({ id: 'createDate', defaultMessage: '创建时间' }) },
  ],
  queryFields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.serviceName`, defaultMessage: '服务名称' }) },
    { name: 'code', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.code`, defaultMessage: '服务编码' }) },
  ],
}));

