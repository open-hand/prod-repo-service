// {
//   projectId,
//   gitlabPipelineId,
//   jobId,
// }
/* eslint-disable import/no-anonymous-default-export */
export default (() => ({
  autoQuery: true,
  selection: false,
  paging: true,
  // transport: {
  //   read: () => ({
  //     // url: '/devops/v1/projects/1/image/1/1',
  //     url: `/devops/v1/projects/${projectId}/image/${gitlabPipelineId}/${jobId}`,
  //     method: 'get',
  //   }),
  // },
  data: [
    {
      vulnerabilityCode: 'motherfuycker',
      severity: 'LOW',
      pkgName: 'hellowoerlr',
      installedVersion: '1.1.1',
      fixedVersion: '12.1',
      description: 'sdsadasdasssssssss',
    },
    {
      vulnerabilityCode: 'motherfuycker',
      severity: 'MEDIUM',
      pkgName: 'hellowoerlr',
      installedVersion: '1.1.1',
      fixedVersion: '12.1',
      description: 'sdsadasdasssssssss',
    },
    {
      vulnerabilityCode: 'motherfuycker',
      severity: 'HIGH',
      pkgName: 'hellowoerlr',
      installedVersion: '1.1.1',
      fixedVersion: '12.1',
      description: 'sdsadasdasssssssss',
    },
    {
      vulnerabilityCode: 'motherfuycker',
      severity: 'CRITICAL',
      pkgName: 'hellowoerlr',
      installedVersion: '1.1.1',
      fixedVersion: '12.1',
      description: 'sdsadasdasssssssss',
    },
  ],
  fields: [
    {
      label: '缺陷码',
      name: 'vulnerabilityCode',
      type: 'string',
    },
    {
      label: '严重度',
      name: 'severity',
      type: 'string',
    },
    {
      label: '组件',
      name: 'pkgName',
      type: 'string',
    },
    {
      label: '当前版本',
      name: 'installedVersion',
      type: 'string',
    },
    {
      label: '修复版本',
      name: 'fixedVersion',
      type: 'string',
    },
    {
      name: 'description',
      type: 'string',
    },
  ],
}));
