/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React from 'react';
import { Button, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { intlPrefix } from '../../../../../index';
import './index.less';

const GuideModal = ({ formatMessage, guideInfo }) => {
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
    <div className="product-lib-selfrepo-guide-modal">
      <div className="product-lib-selfrepo-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pull`, defaultMessage: '拉取' })}
      </div>

      {info.pullServerFlag &&
        <React.Fragment>
          <div className="product-lib-selfrepo-guide-modal-description">
            {'1. 修改maven安装目录下settings.xml文件， 在<servers>code节点添加如下配置， 此处显示的是该仓库不允许匿名访问时的默认拉取用户（复制时显示用户名/密码）'}
          </div>
          <pre><Button icon="content_copy" onClick={() => handleCopy(info.pullServerInfoPassword)} />{info.pullServerInfo}</pre>
        </React.Fragment>
      }

      <div className="product-lib-selfrepo-guide-modal-description">
        {info.pullServerFlag ? '2. ' : '1. '}
        {formatMessage({
          id: `${intlPrefix}.view.configStep2`,
          defaultMessage: '配置项目下的pom.xml文件，在<repositories>节点下添加如下配置',
        })}
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.pullPomRepoInfo)} />{info.pullPomRepoInfo}</pre>

      <div className="product-lib-selfrepo-guide-modal-description">
        {info.pullServerFlag ? '3. ' : '2. '}
        {formatMessage({ id: `${intlPrefix}.view.configPOM`, defaultMessage: '配置pom.xml文件, 在<dependencies>节点下添加如下配置' })}
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.pullPomDep)} />{info.pullPomDep}</pre>
    </div>
  );
};

export default observer(GuideModal);
