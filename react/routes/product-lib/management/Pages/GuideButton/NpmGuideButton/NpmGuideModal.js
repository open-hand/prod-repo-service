/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React from 'react';
import { Button, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './index.less';

export const intlPrefix = 'infra.prod.lib';

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
    <div className="product-lib-npm-guide-modal">
      <div className="product-lib-npm-guide-modal-description">
        1.设置本地npm， 将仓库设置为当前仓库
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.setRegistory)} />{info.setRegistory}</pre>
      <div className="product-lib-npm-guide-modal-description">
        2.命令行登陆
        <span>&quot;个人信息--&gt;个人设置--&gt;制品库设置&quot;中可查看默认密码</span>
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.login)} />{info.login}</pre>
      {!info.hidePush &&
        <React.Fragment>
          <div className="product-lib-selfrepo-guide-modal-second-title">
            {formatMessage({ id: `${intlPrefix}.view.push`, defaultMessage: '推送，发布' })}
          </div>
          <div className="product-lib-npm-guide-modal-description">
            1.准备推送的内容，例如创建package.json，写入
          </div>
          <pre> <Button icon="content_copy" onClick={() => handleCopy(info.packagejson)} />{info.packagejson}</pre>

          <div className="product-lib-npm-guide-modal-description">
            2.推送npm包
          </div>
          <pre> <Button icon="content_copy" onClick={() => handleCopy(info.pushcmd)} />{info.pushcmd}</pre>
        </React.Fragment>
      }

      <div className="product-lib-selfrepo-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pull`, defaultMessage: '拉取' })}
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.pull)} />{info.pull}</pre>

    </div>
  );
};

export default observer(GuideModal);
