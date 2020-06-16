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
              defaultMessage: '1. 修改maven安装目录下settings.xml文件， 在<servers>code节点添加如下配置，',
            })}
            <span style={{ color: 'rgb(239, 78, 66)' }}>
              {formatMessage({
                id: `${intlPrefix}.view.configStep11`,
                defaultMessage: '此处显示的是该仓库不允许匿名访问时的默认拉取用户（复制时会显示用户名/密码）',
              })}
            </span>
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

      <div className="product-lib-org-lib-list-guide-modal-description">
        {info.pullServerFlag ? '3. ' : '2. '}
        {formatMessage({ id: `${intlPrefix}.view.configPOM`, defaultMessage: '配置pom.xml文件' })}
      </div>
      <pre><Icon type="content_copy" onClick={() => handleCopy(info.pullPomDep)} />{info.pullPomDep}</pre>
    </div>
  );
};

export default observer(GuideModal);
