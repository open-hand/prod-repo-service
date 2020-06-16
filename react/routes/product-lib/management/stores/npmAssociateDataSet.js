export default (formatMessage => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {},
  fields: [
    {
      name: 'userName',
      type: 'string',
      label: formatMessage({ id: 'userName' }),
    },
    {
      name: 'password',
      type: 'string',
      label: formatMessage({ id: 'password' }),
    },
  ],
  queryFields: [],
}));
