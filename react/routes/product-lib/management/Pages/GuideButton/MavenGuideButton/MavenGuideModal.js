/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React from 'react';
import { Button, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { stores } from '@choerodon/boot';
import { intlPrefix } from '../../../index';
import './index.less';

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

  const { currentMenuType: { organizationId } } = stores.AppState;
  
  return (
    <div className="product-lib-selfrepo-guide-modal">
      <div className="product-lib-selfrepo-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.pull`, defaultMessage: '拉取' })}
      </div>
      {info.pullServerFlag &&
        <React.Fragment>
          <div className="product-lib-selfrepo-guide-modal-description">
            {'1. 修改maven安装目录下settings.xml文件， 在<servers>code节点添加如下配置，'}
            <span style={{ color: 'rgb(239, 78, 66)' }}>此处显示的是该仓库不允许匿名访问时的默认拉取用户（复制时显示用户名/密码）</span>
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
      <div className="product-lib-selfrepo-guide-modal-description" style={{ marginBottom: '13px' }}>
        {info.pullServerFlag ? '3. ' : '2. '}
        {formatMessage({
          id: `${intlPrefix}.view.configStep3`,
          defaultMessage: '配置项目下的pom.xml文件，添加maven依赖包坐标，具体坐标请到包列表中查看',
        })}
      </div>
      {info.showPushFlag &&
        <React.Fragment>
          <div className="product-lib-selfrepo-guide-modal-second-title">
            {formatMessage({ id: `${intlPrefix}.view.push`, defaultMessage: '推送，发布' })}
          </div>
          <div className="product-lib-selfrepo-guide-modal-description">
            {'1. 修改maven安装目录下settings.xml文件，在<servers>节点添加如下配置（替换用户名密码为自己的）'}
            &quot;
            <a
              target="_blank"
              rel="noreferrer"
              href={`#/rducm/personal-setting?type=site&organizationId=${organizationId}`}
            >
              个人信息--&gt;个人设置
            </a>--&gt; 制品库设置&quot;中可查看默认密码
          </div>
          <pre><Button icon="content_copy" onClick={() => handleCopy(info.pushServerInfoPassword)} />{info.pushServerInfo}</pre>
          <div className="product-lib-selfrepo-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.pushConfigStep2`,
              defaultMessage: '2. 配置项目下的pom.xml文件',
            })}
          </div>
          <pre><Button icon="content_copy" onClick={() => handleCopy(info.pushPomManageInfo)} />{info.pushPomManageInfo}</pre>
          <div className="product-lib-selfrepo-guide-modal-description">
            {formatMessage({
              id: `${intlPrefix}.view.pushConfigStep3`,
              defaultMessage: '3. 项目根目录下，运行命令',
            })}
          </div>
          <pre><Button icon="content_copy" onClick={() => handleCopy(info.pushCmd)} />{info.pushCmd}</pre>
        </React.Fragment>
      }
    </div>
  );
};

export default observer(GuideModal);
