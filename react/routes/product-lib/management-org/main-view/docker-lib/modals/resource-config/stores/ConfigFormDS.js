
export default ({ formatMessage, intlPrefix, projectId, repoName }) => ({
  autoQuery: true,
  autoCreate: true,
  transport: {
    read: () => {
      const url = repoName === '全部仓库' ? '/rdupm/v1/harbor-quota/global' : `/rdupm/v1/harbor-quota/project/${projectId}`;
      return {
        url,
        method: 'GET',
      };
    },
    submit: ({ data: [data] }) => {
      const params = `?countLimit=${data.countLimit}&storageNum=${data.storageNum}&storageUnit=${data.storageUnit}`;
      const url = repoName === '全部仓库' ? `/rdupm/v1/harbor-quota/update-global${params}` : `/rdupm/v1/harbor-quota/update-project/${projectId}${params}`;
      return {
        url,
        method: 'post',
        data: null,
      };
    },
  },
  fields: [
    {
      name: 'repoName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
      defaultValue: repoName,
    },
    {
      name: 'countLimit',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.artifactoryCount` }),
      min: -1,
      step: 1,
    },
    {
      name: 'storageNum',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.storage` }),
      min: -1,
      max: 1024,
      step: 1,
    },
    {
      name: 'storageUnit',
      type: 'string',
      label: formatMessage({ id: 'unit' }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUPM.STORAGE_UNIT',
    },
  ],
});
