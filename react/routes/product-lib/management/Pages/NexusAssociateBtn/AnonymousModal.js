import React from 'react';
import { observer } from 'mobx-react-lite';
import Anonymous1 from './img/anonymous-1.jpg';
import Anonymous2 from './img/anonymous-2.jpg';
import Anonymous3 from './img/anonymous-3.jpg';
import Anonymous4 from './img/anonymous-4.jpg';


const AnonymousModal = () => {
  const launchFullScreen = (element) => {
    // 先检测最标准的方法
    const el = element.target;
    if (el.requestFullScreen) {
      el.requestFullScreen();
    } else if (el.mozRequestFullScreen) {
      // 其次，检测Mozilla的方法
      el.mozRequestFullScreen();
    } else if (el.webkitRequestFullscreen) {
      // if 检测 webkit的API
      el.webkitRequestFullScreen();
    }
  };
  
  return (
    <div className="product-lib-anonymous-guide-modal">

      <div className="product-lib-anonymous-guide-modal-second-title">
        说明
      </div>

      <div className="product-lib-anonymous-guide-modal-description">
        如果需要开启匿名访问控制，需要在对应nexus服务上做相应配置。
        nexus3版本及其以上，如果服务上允许匿名访问，默认有一个匿名访问用户，但这个用户拥有所有仓库的访问权限，故需要更改此处设置
      </div>
      <div
        className="product-lib-anonymous-guide-modal-description"
        style={{ height: 'auto', marginBottom: '12px' }}
      >
        <img onClick={launchFullScreen} src={Anonymous1} alt="" />
      </div>


      <div className="product-lib-anonymous-guide-modal-second-title">
        配置
      </div>

      <div className="product-lib-anonymous-guide-modal-description">
        1. 创建一个用户匿名访问的角色，如：test-anonymous。将允许匿名访问仓库的read、browse权限给到这个用户
      </div>

      <div
        className="product-lib-anonymous-guide-modal-description"
        style={{ height: 'auto', marginBottom: '12px' }}
      >
        <img onClick={launchFullScreen} src={Anonymous2} alt="" />
      </div>
      <div className="product-lib-anonymous-guide-modal-description">
        2. 创建一个用户，将上述角色赋予这个用户。并将匿名访问的用户设置为该新建的用户
      </div>
      <div
        className="product-lib-anonymous-guide-modal-description"
        style={{ height: 'auto', marginBottom: '12px' }}
      >
        <img onClick={launchFullScreen} src={Anonymous3} alt="" />
      </div>
      <div
        className="product-lib-anonymous-guide-modal-description"
        style={{ height: 'auto', marginBottom: '12px' }}
      >
        <img onClick={launchFullScreen} src={Anonymous4} alt="" />
      </div>
    </div>
  );
};


export default observer(AnonymousModal);
