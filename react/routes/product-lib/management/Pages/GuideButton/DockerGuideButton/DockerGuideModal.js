/**
* docker配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/28
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
    <div className="product-lib-docker-guide-modal">

      <div className="product-lib-docker-guide-modal-second-title">
        Docker客户端配置
      </div>

      <div className="product-lib-docker-guide-modal-description">
        1. 软件安装
      </div>
      <div className="product-lib-docker-guide-modal-description" style={{ height: 'auto', marginBottom: '12px' }}>
        docker版本不小于1.10.0，docker-compose版本不小于1.6.0。具体安装教程参考：https://docs.docker.com/engine/install/centos/
      </div>


      {/* <div className="product-lib-docker-guide-modal-description">
        3. 下载证书
      </div>
      <div className="product-lib-docker-guide-modal-description" style={{ height: 'auto', marginBottom: '12px' }}>
        首先在docker主机上创建证书目录。
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.mkdirCertCmd)} />{info.mkdirCertCmd}</pre>
      <div className="product-lib-docker-guide-modal-description" style={{ height: 'auto', marginBottom: '12px' }}>
        然后下载证书，将下载的.cert 和 .key 文件拷贝到上一步创建的目录中。
      </div>

      <Button icon="get_app" onClick={() => window.open(info.certUrl)}>下载.CERT文件</Button>
      <Button icon="get_app" onClick={() => window.open(info.keyUrl)}>下载.KEY文件</Button> */}


      <div className="product-lib-docker-guide-modal-description">
        2. docker主机上新增/etc/docker/daemon.json 并增加配置
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.configRegistryCmd)} />{info.configRegistryCmd}</pre>

      <div className="product-lib-docker-guide-modal-description">
        3. 重启docker
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy('systemctl daemon-reload\nsystemctl restart docker')} />
        {'systemctl daemon-reload\nsystemctl restart docker'}
      </pre>


      <div className="product-lib-docker-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.buildAndPush`, defaultMessage: '构建镜像并push' })}
      </div>

      <div className="product-lib-docker-guide-modal-description">
        1. 打开docker客户端，使用harbor用户名密码登陆harbor访问地址。
        &quot;
        <a
          target="_blank"
          rel="noreferrer"
          href={`#/rducm/personal-setting?type=site&organizationId=${organizationId}`}
        >
          个人信息--&gt;个人设置
        </a>--&gt; 制品库设置&quot;中可查看默认密码
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.loginCmd)} />{info.loginCmd}</pre>

      <div className="product-lib-docker-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.docker.buildAndPush.configStep2`,
          defaultMessage: '2. 创建Dockerfile构建镜像',
        })}
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.dockerFile)} />{info.dockerFile}</pre>


      <div className="product-lib-docker-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.docker.buildAndPush.configStep3`,
          defaultMessage: '3. 使用Dockerfile构建镜像，打tag',
        })}
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.buildCmd)} />{info.buildCmd}</pre>


      <div className="product-lib-docker-guide-modal-description">
        {formatMessage({
          id: `${intlPrefix}.view.docker.buildAndPush.configStep4`,
          defaultMessage: '4. 推送到远程仓库',
        })}
      </div>
      <pre> <Button icon="content_copy" onClick={() => handleCopy(info.pushCmd)} />{info.pushCmd}</pre>


      <div className="product-lib-docker-guide-modal-second-title">
        {formatMessage({ id: `${intlPrefix}.view.docker.pull`, defaultMessage: 'Pull镜像' })}
      </div>
      <pre><Button icon="content_copy" onClick={() => handleCopy(info.pullCmd)} />{info.pullCmd}</pre>

    </div>
  );
};

export default observer(GuideModal);
