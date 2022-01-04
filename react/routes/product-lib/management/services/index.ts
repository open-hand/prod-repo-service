import { axios } from '@choerodon/master';
import ProductLibProjectApis from '@/routes/product-lib/management/apis';

interface batchDeleteProp {
  organizationId: number,
  projectId: number,
  repositoryId: string,
  postData: Array<{ componentIds: string[] | never } | never>,
}

export default class ProductLibProjectServices {
  static batchDelete({
    organizationId, projectId, postData, repositoryId,
  }: batchDeleteProp) {
    return axios({
      url: ProductLibProjectApis.batchDelete(organizationId, projectId, repositoryId),
      method: 'delete',
      data: postData,
    });
  }
}
