/**
 * 制品库项目层
 * @author JZH <zhihao.jiang@hand-china.com>
 * @creationDate 2020/4/38
 * @copyright 2020 ® HAND
 */
import React, { useMemo, useContext, useState, useRef } from 'react';
import { Header, Content, Breadcrumb } from '@choerodon/boot';
import { Button, Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { useCheckPermission } from '@/utils';
import { useStore } from '../../index';
import { RepositoryIdContext } from './../index';
import { OverView, ImageList, AuthList, OptLog } from './DockerTabs';
import { DockerGuideButton } from '../GuideButton';
import { DockerAddMemberButton } from '../AddAuthButton';

const { TabPane } = Tabs;

export const TabKeyEnum = {
  OVERVIEW: 'overview',
  DOCKER_IMAGE: 'dockerImage',
  USER_AUTH: 'userAuth',
  OPT_LOG: 'optLog',
};

const DockerTabContainer = (props) => {
  const overviewRef = useRef();
  const { harborId, name } = useContext(RepositoryIdContext);
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.OVERVIEW);

  const allPermission = useCheckPermission([
    'choerodon.code.project.infra.product-lib.ps.project-member-harbor',
    'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
  ]);

  const ownerPermission = useCheckPermission([
    'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
  ]);

  const {
    intlPrefix,
    intl: { formatMessage },
    dockerImageListDs,
    dockerImageTagDs,
    dockerImageScanDetailsDs,
    dockerAuthDs,
    optLogDs,
  } = useStore();

  const guideButtonProps = useMemo(() => ({ formatMessage }), [formatMessage]);

  const overViewProps = useMemo(() => ({ harborId, formatMessage, activeTabKey }), [formatMessage, activeTabKey, harborId]);

  const imageListProps = useMemo(() => ({ 
    dockerImageTagDs, 
    dockerImageListDs, 
    harborId, 
    formatMessage, 
    activeTabKey,
    dockerImageScanDetailsDs,
    // getCurrentTheme,
  }), [dockerImageTagDs, dockerImageListDs, activeTabKey, harborId]);

  const authListProps = useMemo(() => ({ dockerAuthDs, formatMessage, activeTabKey }), [dockerAuthDs, formatMessage, activeTabKey]);
  const optLogProps = useMemo(() => ({ optLogDs, formatMessage, activeTabKey }), [optLogDs, formatMessage, activeTabKey]);

  const addMemberButtonProps = useMemo(() => ({ dockerAuthDs, formatMessage }), [dockerAuthDs, formatMessage]);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      overviewRef.current.init(harborId);
    } else if (activeTabKey === TabKeyEnum.DOCKER_IMAGE) {
      dockerImageListDs.query();
    } else if (activeTabKey === TabKeyEnum.USER_AUTH) {
      dockerAuthDs.query();
    } else {
      optLogDs.query();
    }
  };

  return (
    <React.Fragment>
      <Header>
        <DockerGuideButton {...guideButtonProps} />
        {activeTabKey === TabKeyEnum.USER_AUTH && <DockerAddMemberButton {...addMemberButtonProps} />}
        <Button
          icon="refresh"
          onClick={refresh}
        >
          {formatMessage({ id: 'refresh' })}
        </Button>
      </Header>

      <Breadcrumb title={name} />

      <Content className="product-lib-management">
        <Tabs
          activeKey={activeTabKey}
          animated={false}
          onChange={newActiveKey => setActiveTabKey(newActiveKey)}
          className="product-lib-management-tabs"
        >
          {allPermission &&
            [
              <TabPane tab={formatMessage({ id: `${intlPrefix}.view.overviewRepo`, defaultMessage: '仓库总览' })} key={TabKeyEnum.OVERVIEW}>
                <OverView ref={overviewRef} {...overViewProps} />
              </TabPane>,
              <TabPane tab={formatMessage({ id: `${intlPrefix}.view.dockerImageList`, defaultMessage: '镜像列表' })} key={TabKeyEnum.DOCKER_IMAGE}>
                <ImageList {...imageListProps} />
              </TabPane>,
              <TabPane tab={formatMessage({ id: `${intlPrefix}.view.userAuth`, defaultMessage: '用户权限' })} key={TabKeyEnum.USER_AUTH}>
                <AuthList {...authListProps} />
              </TabPane>,
            ]
          }
          {ownerPermission &&
            <TabPane tab={formatMessage({ id: `${intlPrefix}.view.optLog`, defaultMessage: '操作日志' })} key={TabKeyEnum.OPT_LOG}>
              <OptLog {...optLogProps} activeRepository={props.activeRepository} />
            </TabPane>
          }
        </Tabs>
      </Content>
    </React.Fragment >
  );
};

export default observer(DockerTabContainer);
