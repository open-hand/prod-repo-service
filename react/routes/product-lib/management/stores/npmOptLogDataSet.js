export default (intlPrefix, formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'logId',
  transport: {
    read: () => ({
      url: `/rdupm/v1/${organizationId}/nexus-logs/org/project`,
      method: 'GET',
    }),
  },
  fields: [],
  queryFields: [
    {
      name: 'projectId',
      type: 'string',
    },
    {
      name: 'startDate',
      type: 'date',
      max: 'endDate',
      label: formatMessage({ id: `${intlPrefix}.model.startDate` }),
    },
    {
      name: 'endDate',
      type: 'date',
      min: 'startDate',
      label: formatMessage({ id: `${intlPrefix}.model.endDate` }),
    },
  ],
});
