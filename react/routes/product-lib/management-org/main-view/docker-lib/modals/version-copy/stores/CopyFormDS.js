
import omit from 'lodash/omit';

export default ((intlPrefix, formatMessage, organizationId, srcRepoName, digest, repoListDs) => {
  async function checkImageName(value) {
    const pattern = /[a-z0-9]+\(?[.-][a-z0-9]+\)*./;
    let errorMsg;
    if (value) {
      if (!pattern.test(value)) {
        errorMsg = formatMessage({ id: `${intlPrefix}.validate.destImageName` });
      }
      return errorMsg;
    }
  }
  async function checkTagName(value) {
    const pattern = /([\w][\w.-]{0,127})/;
    let errorMsg;
    if (value) {
      if (!pattern.test(value)) {
        errorMsg = formatMessage({ id: `${intlPrefix}.validate.destImageTagName` });
      }
      return errorMsg;
    }
  }
  return ({
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
        options: repoListDs,
        // lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
      },
      {
        name: 'destImageName',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.model.destImageName` }),
        validator: checkImageName,
      },
      {
        name: 'destImageTagName',
        type: 'string',
        required: true,
        validator: checkTagName,
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
  });
});
