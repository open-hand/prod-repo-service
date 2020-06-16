
import React from 'react';
import { Icon, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './index.less';

const GuideModal = ({ name, version, repositoryUrl, intlPrefix, formatMessage }) => {
  const setInfo = `npm config set registry=${repositoryUrl}`;
  const loginInfo = 'npm login';
  const pullInfo = `npm install ${name}@${version} --registry=${repositoryUrl}`;

  const handleCopy = (content) => {
    const oInput = document.createElement('textarea');
    oInput.value = content;
    document.body.appendChild(oInput);
    oInput.select(); // 选择对象
    document.execCommand('Copy'); // 执行浏览器复制命令
    document.body.removeChild(oInput);
    message.success(formatMessage({ id: 'success.copy', defaultMessage: '复制成功' }), 1);
  };

  return (
    <div className="product-lib-org-npm-tag-guide-modal">
      <div className="product-lib-org-npm-tag-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pullTagGuide`, defaultMessage: '拉取指引' })}
      </div>
      <div className="product-lib-org-npm-tag-guide-modal-description">
        1.设置本地npm， 将仓库设置为当前仓库
      </div>
      <pre> <Icon type="content_copy" onClick={() => handleCopy(setInfo)} />{setInfo}</pre>
      <div className="product-lib-org-npm-tag-guide-modal-description">
        2.命令行登陆
      </div>
      <pre> <Icon type="content_copy" onClick={() => handleCopy(loginInfo)} />
        <span>{formatMessage({ id: `${intlPrefix}.view.loginTips` })}</span><br />
        {loginInfo}
      </pre>

      <div className="product-lib-org-npm-tag-guide-modal-description">
        3.拉取
      </div>
      <pre> <Icon type="content_copy" onClick={() => handleCopy(pullInfo)} />{pullInfo}</pre>
    </div>
  );
};

export default observer(GuideModal);
