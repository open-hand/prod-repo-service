export default (({ tagName, repoName }) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-guide/tag?digest=${tagName}&repoName=${repoName}`,
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'loginCmd',
      type: 'string',
    },
    {
      name: 'pullCmd',
      type: 'string',
    },
  ],
}));
