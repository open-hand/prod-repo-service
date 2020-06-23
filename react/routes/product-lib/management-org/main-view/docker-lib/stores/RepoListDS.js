export default ({ organizationId }) => ({
  paging: false,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-project/all/${organizationId}`,
      method: 'get',
    }),
  },
});
