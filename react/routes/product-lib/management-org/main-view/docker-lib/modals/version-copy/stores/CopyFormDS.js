
import omit from 'lodash/omit';

export default ((intlPrefix, formatMessage, organizationId, srcRepoName, digest) => ({
  autoQuery: false,
  transport: {
    create: ({ data: [data] }) => {
      const postData = omit(data, '__id', '__status');
      return {
        url: '/rdupm/v1/harbor-image-tag/copy',
        method: 'post',
        data: postData,
      };
    },
  },
  fields: [
    {
      name: 'destProjectCode',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.destProject` }),
      textField: 'name',
      valueField: 'code',
      lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    },
    {
      name: 'destImageName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.destImageName` }),
    },
    {
      name: 'destImageTagName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.destImageTagName` }),
    },
    {
      name: 'srcRepoName',
      type: 'string',
      defaultValue: srcRepoName,
    },
    {
      name: 'digest',
      type: 'string',
      defaultValue: digest, // TODO 这个需要？
    },
  ],
  queryFields: [],
}));
