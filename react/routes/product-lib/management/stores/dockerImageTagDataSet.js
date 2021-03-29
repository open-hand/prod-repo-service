export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  // selection: true,
  page: 10,
  // idField: 'id',
  // parentField: 'parentId',
  // transport: {
  //   read: () => ({
  //     url: `/rdupm/v1/harbor-image-tag/list/${projectId}`,
  //     method: 'GET',
  //   }),
  // },
  data: [
    {
      id: 1213,
      digest: 'sha256:c17ed63ab5ec57167ab49e7ff39ae090346b308119ae8b03fafa545da54060b1',
      size: 2458171,
      architecture: 'amd64',
      os: 'linux',
      dockerVersion: 'v2.1.3-b6de84c5',
      pushTime: '2021-03-18T08:53:51.321Z',
      pullTime: '2021-03-22T10:14:55.278Z',
      sizeDesc: '2.34MB',
      versions: [
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
      ],
      extraAttrs: {
        architecture: 'amd64',
        author: null,
        os: 'linux',
      },
      parentId: null,
    },
    {
      id: 12132,
      parentId: 1213,
      digest: 'sha256:c17ed63ab5ec57167ab49e7ff39ae090346b308119ae8b03fafa545da54060b1',
      size: 2458171,
      architecture: 'amd64',
      os: 'linux',
      dockerVersion: 'v2.1.3-b6de84c5',
      pushTime: '2021-03-18T08:53:51.321Z',
      pullTime: '2021-03-22T10:14:55.278Z',
      sizeDesc: '2.34MB',
      versions: [
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
        {
          version: '2020.7.27-182207-master',
          date: '2021.11.10 11:10:09',
          pDate: '2021.11.10 11:10:09',
        },
      ],
      extraAttrs: {
        architecture: 'amd64',
        author: null,
        os: 'linux',
      },
    },
  ],
  fields: [
    { name: 'id', type: 'number' },
    // { name: 'parentId', type: 'number', parentFieldName: 'id' },
    { name: 'dockerVersion', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
    { name: 'sizeDesc', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.sizeDesc`, defaultMessage: '大小' }) },
    { name: 'dockerVersion', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.dockerVersion`, defaultMessage: 'Docker版本' }) },
    { name: 'os', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.OS/ARCH`, defaultMessage: 'OS/ARCH' }) },
    { name: 'architecture', type: 'string' },
    { name: 'digest', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.digest`, defaultMessage: '摘要' }) },
    { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName1`, defaultMessage: '推送者' }) },
    { name: 'createTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.createTime`, defaultMessage: '创建时间' }) },
    { name: 'pushTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pushTime`, defaultMessage: '最新推送时间' }) },
    { name: 'pullTime', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.pullTime`, defaultMessage: '最近拉取时间' }) },
  ],
  queryFields: [
    { name: 'dockerVersion', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
  ],
}));
