export default ({ organizationId }) => ({
  paging: false,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-repositorys/organizations/${organizationId}/maven/repo/name`,
      method: 'get',
    }),
  },
});
