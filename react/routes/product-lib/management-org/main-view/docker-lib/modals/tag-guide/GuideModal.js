/**
* Docker镜像Tag拉取指引
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/30
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { message, Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const GuideModal = ({ tagPullDs, formatMessage, organizationId }) => {
  function refresh() {
    tagPullDs.query();
  }
  useEffect(() => {
    refresh();
  }, []);
  if (!tagPullDs.current) return;
  const record = tagPullDs.current;

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
    <div className="product-lib-org-lib-list-guide-modal">
      <div className="product-lib-org-lib-list-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pullTagGuide`, defaultMessage: '拉取指引' })}
      </div>

      <div className="product-lib-org-lib-list-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.pullTagGuide.configStep1`,
          defaultMessage: '1. 登陆制品库，认证用户',
        })}。
        <a
          target="_blank"
          rel="noreferrer"
          href={`#/rducm/personal-setting/product?type=site&organizationId=${organizationId}`}
        >
          {formatMessage({ id: `${intlPrefix}.view.personal.setting` })}
        </a>
        {formatMessage({ id: `${intlPrefix}.view.personal.tips` })}
      </div>
      <pre><Icon type="content_copy" onClick={() => handleCopy(record.get('loginCmd'))} />{record.get('loginCmd')}</pre>

      <div className="product-lib-org-lib-list-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.pullTagGuide.configStep2`,
          defaultMessage: '2. 执行命令',
        })}
      </div>
      <pre> <Icon type="content_copy" onClick={() => handleCopy(record.get('pullCmd'))} />{record.get('pullCmd')}</pre>
    </div>
  );
};

export default observer(GuideModal);
