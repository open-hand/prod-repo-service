

export default ((formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/${organizationId}/nexus-logs/org-log?repoType=MAVEN`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'userName',
      type: 'string',
    },
    {
      name: 'name',
      type: 'string',
    },
    {
      name: 'resourcePath',
      type: 'string',
    },
    {
      name: 'authority',
      type: 'string',
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUDM.AUTH_LEVEL',
    },
    {
      name: 'operateType',
      type: 'string',
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUDM.OPERATE_TYPE',
    },
    {
      name: 'projectImageUrl',
      type: 'string',
    },
    {
      name: 'projectName',
      type: 'string',
    },
    {
      name: 'operator',
      type: 'string',
    },
    {
      name: 'date',
      type: 'string',
    },
    {
      name: 'time',
      type: 'string',
    },
    {
      name: 'content',
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'loginName',
      type: 'string',
    },
    {
      name: 'code',
      type: 'string',
      // required: true,
      textField: 'name',
      valueField: 'code',
      lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    },
    {
      name: 'name',
      type: 'string',
    },
    {
      name: 'operateType',
      type: 'string',
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUDM.OPERATE_TYPE',
    },
    {
      name: 'date',
      type: 'date',
      range: ['startDate', 'endDate'],
    },
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
}));
