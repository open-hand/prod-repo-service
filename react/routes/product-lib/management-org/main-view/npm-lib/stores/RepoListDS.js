export default ({ organizationId }) => ({
  paging: false,
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-repositorys/organizations/${organizationId}/npm/repo/name`,
      method: 'get',
    }),
  },
});
