export default class ProductLibProjectApis {
  static batchDelete(organizationId: number, projectId: number, repositoryId: string) {
    return `/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/batch?repositoryId=${repositoryId}`;
  }
}
