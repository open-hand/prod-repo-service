export default ((intlPrefix, formatMessage, organizationId, repoListDs) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rdupm/v1/harbor-image/list-org/${organizationId}`,
      method: 'GET',
    }),
    // destroy: ({ data: [data] }) => ({
    //   url: '/rdupm/v1/harbor-image/delete',
    //   method: 'delete',
    //   data,
    // }),
  },
  fields: [
    {
      name: 'imageName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.imageName` }),
    },
    {
      name: 'projectName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repoName` }),
    },
    {
      name: 'updateTime',
      type: 'dataTime',
      label: formatMessage({ id: `${intlPrefix}.model.updateTime` }),
    },
    {
      name: 'tagsCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.tagsCount` }),
    },
    {
      name: 'pullCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.pullCount` }),
    },
    {
      name: 'description',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.description` }),
    },
  ],
  queryFields: [
    {
      name: 'code',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
      required: true,
      textField: 'name',
      valueField: 'code',
      options: repoListDs,
      // lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    },
    // {
    //   name: 'name',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
    //   required: true,
    //   textField: 'name',
    //   valueField: 'name',
    //   lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    // },
    {
      name: 'imageName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.imageName` }),
    },
  ],
}));
