/**
* 制品库配置指引
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React from 'react';
import { Icon, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './index.less';

const intlPrefix = 'infra.prod.lib';

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
    <div className="product-lib-org-lib-list-guide-modal">
      <div className="product-lib-org-lib-list-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pull`, defaultMessage: '拉取' })}
      </div>
      {info.pullServerFlag &&
        <React.Fragment>
          <div className="product-lib-org-lib-list-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.configStep1`,
              defaultMessage: '1. 配置setting.xml文件，若该仓库不允许匿名访问，在<servers>code节点添加如下配置。若允许匿名，该步骤忽略',
            })}
          </div>
          <pre><Icon type="content_copy" onClick={() => handleCopy(info.pullServerInfoPassword)} />{info.pullServerInfo}</pre>
        </React.Fragment>
      }
      <div className="product-lib-org-lib-list-guide-modal-description">
        {info.pullServerFlag ? '2. ' : '1. '}
        {formatMessage({
          id: `${intlPrefix}.view.configStep2`,
          defaultMessage: '配置项目下的pom.xml文件，在<repositories>节点下添加如下配置',
        })}
      </div>
      <pre> <Icon type="content_copy" onClick={() => handleCopy(info.pullPomRepoInfo)} />{info.pullPomRepoInfo}</pre>
      {info.showPushFlag &&
        <React.Fragment>
          <div className="product-lib-org-lib-list-guide-modal-second-title">
            {formatMessage({ id: `${intlPrefix}.view.push`, defaultMessage: '推送，发布' })}
          </div>
          <div className="product-lib-org-lib-list-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.pushConfigStep1`,
              defaultMessage: '1. 修改maven安装目录下settings.xml文件，在<servers>节点添加如下配置（替换用户名密码为自己的）',
            })}
          </div>
          <pre><Icon type="content_copy" onClick={() => handleCopy(info.pushServerInfoPassword)} />{info.pushServerInfo}</pre>
          <div className="product-lib-org-lib-list-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.pushConfigStep2`,
              defaultMessage: '2. 配置项目下的pom.xml文件，在<repositories>节点下添加如下配置',
            })}
          </div>
          <pre><Icon type="content_copy" onClick={() => handleCopy(info.pushPomManageInfo)} />{info.pushPomManageInfo}</pre>
          <div className="product-lib-org-lib-list-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.pushConfigStep3`,
              defaultMessage: '3. 项目根目录下，运行命令',
            })}
          </div>
          <pre><Icon type="content_copy" onClick={() => handleCopy(info.pushCmd)} />{info.pushCmd}</pre>
        </React.Fragment>
      }
    </div>
  );
};

export default observer(GuideModal);
