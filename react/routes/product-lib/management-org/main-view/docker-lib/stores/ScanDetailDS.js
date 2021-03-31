// {
//   projectId,
//   gitlabPipelineId,
//   jobId,
// }

import { get } from 'lodash';

/* eslint-disable import/no-anonymous-default-export */
export default (({ organizationId }) => ({
  autoQuery: false,
  selection: false,
  paging: true,
  pageSize: 10,
  transport: {
    read: ({ data }) => ({
      url: `/rdupm/v1/harbor-image/organization/${organizationId}/scan-images-detail`,
      method: 'post',
      data: {
        digest: get(data, 'digest'),
        tagName: get(data, 'tagName'),
        repoName: get(data, 'repoName'),
      },
    }),
  },
  fields: [
    {
      label: '缺陷码',
      name: 'id',
      type: 'string',
    },
    {
      label: '严重度',
      name: 'severity',
      type: 'string',
    },
    {
      label: '组件',
      name: 'packageStr',
      type: 'string',
    },
    {
      label: '当前版本',
      name: 'version',
      type: 'string',
    },
    {
      label: '修复版本',
      name: 'fixVersion',
      type: 'string',
    },
    {
      name: 'description',
      type: 'string',
    },
  ],
}));
