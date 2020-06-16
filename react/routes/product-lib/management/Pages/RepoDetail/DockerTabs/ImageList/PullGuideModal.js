/**
* docker镜像tag配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React from 'react';
import { Button, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';

const intlPrefix = 'infra.prod.lib';

const GuideModal = ({ guideInfo, formatMessage }) => {
  const { info } = guideInfo;

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
    <div className="product-lib-guide-modal">
      <div className="product-lib-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pullTagGuide`, defaultMessage: '拉取指引' })}
      </div>

      <div className="product-lib-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.pullTagGuide.configStep1`,
          defaultMessage: '1. 登陆制品库，认证用户',
        })}
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.loginCmd)} />{info.loginCmd}</pre>

      <div className="product-lib-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.pullTagGuide.configStep2`,
          defaultMessage: '2. 执行命令',
        })}
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.pullCmd)} />{info.pullCmd}</pre>
    </div>
  );
};

export default observer(GuideModal);
