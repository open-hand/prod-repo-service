import { validateUrl } from '@/utils';
import { axios } from '@choerodon/boot';

export default ((intlPrefix, formatMessage, projectId) => {
  async function checkoutDocker(value) {
    try {
      const res = await axios.post(`/rdupm/v1/check/name/${projectId}`, JSON.stringify({
        repositoryName: value,
      }));
      if (!res) {
        return 'docker仓库重名';
      }
      return res;
    } catch (error) {
      throw new Error(error);
    }
  }

  return {
    autoQuery: false,
    selection: false,
    pageSize: 10,
    transport: {},
    fields: [
      {
        name: 'repoName',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.model.repoName`, defaultMessage: '仓库名称' }),
        validator: checkoutDocker,
      },
      {
        name: 'repoUrl',
        type: 'string',
        required: true,
        validator: (value) => validateUrl(value),
        label: formatMessage({ id: `${intlPrefix}.model.repoUrl`, defaultMessage: '仓库地址' }),
      },
      {
        name: 'description',
        type: 'string',
        label: formatMessage({ id: 'description', defaultMessage: '描述' }),
      },
      {
        name: 'loginName',
        type: 'string',
        required: true,
        label: formatMessage({ id: 'loginName', defaultMessage: '登录名' }),
      },
      {
        name: 'password',
        type: 'string',
        required: true,
        label: formatMessage({ id: 'password', defaultMessage: '密码' }),
      },
      {
        name: 'email',
        type: 'string',
        required: true,
        label: formatMessage({ id: 'mailbox', defaultMessage: '邮箱' }),
      },
      {
        name: 'projectShare',
        type: 'string',
        required: true,
        defaultValue: 'false',
        label: formatMessage({ id: `${intlPrefix}.model.projectShare`, defaultMessage: '是否共享' }),
      },
    ],
  };
});
