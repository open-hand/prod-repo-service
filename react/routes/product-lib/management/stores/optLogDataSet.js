export default (projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: ({ dataSet }) => ({
      url: dataSet.logTabKey === 'AuthLog' ?
        `/rdupm/v1/harbor-logs/auth/list-project/${projectId}`
        : `/rdupm/v1/harbor-logs/image/list-project/${projectId}`,
      method: 'GET',
    }),
  },
  fields: [],
  queryFields: [
    {
      name: 'startDate',
      type: 'date',
      max: 'endDate',
    },
    {
      name: 'endDate',
      type: 'date',
      min: 'startDate',
    },
  ],
});
