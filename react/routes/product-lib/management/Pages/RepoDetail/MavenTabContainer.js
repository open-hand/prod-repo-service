/**
 * 制品库项目层
 * @author JZH <zhihao.jiang@hand-china.com>
 * @creationDate 2020/3/31
 * @copyright 2020 ® HAND
 */
import React, { useMemo, useContext, useState } from 'react';
import { Header, Content, Breadcrumb } from '@choerodon/boot';
import { Button, Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { useCheckPermission } from '@/utils';
import { useStore } from '../../index';
import { RepositoryIdContext } from './../index';
import { OverView, NexusComponent, PublishAuth, OptLog } from './MavenTabs';
import { MavenGuideButton } from '../GuideButton';
import { MavenUploadButton } from '../UploadPackageButton';
import { MavenAddMemberButton } from '../AddAuthButton';

const { TabPane } = Tabs;

export const TabKeyEnum = {
  OVERVIEW: 'overview',
  NEXUS_COMPONENT: 'nexusComponent',
  PUBLIST_AUTH: 'publishAuth',
  OPTLOG: 'optLog',
};


const MavenTabContainer = () => {
  const { repositoryId, repositoryName, name, versionPolicy, type, enableFlag } = useContext(RepositoryIdContext);
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.OVERVIEW);
  const {
    intlPrefix,
    intl: { formatMessage },
    overViewDs,
    nexusComponentDs,
    publishAuthDs,
    mavenUploadPackageDs,
    npmOptLogDs: mavenOptLogDs,
  } = useStore();

  const ownerPermission = useCheckPermission(['choerodon.code.project.infra.product-lib.ps.project-owner-maven']);

  const uploadPackageButtonProps = useMemo(() => ({ repositoryId, repositoryName, mavenUploadPackageDs, formatMessage, nexusComponentDs }), [repositoryId, repositoryName, mavenUploadPackageDs, nexusComponentDs, formatMessage]);
  const guideButtonProps = useMemo(() => ({ repositoryId, formatMessage, name: repositoryName }), [repositoryId, formatMessage, repositoryName]);


  const overViewProps = useMemo(() => ({ repositoryId, overViewDs, formatMessage, activeTabKey }), [overViewDs, formatMessage, activeTabKey, repositoryId]);
  const nexusComponentProps = useMemo(() => ({ repositoryId, repositoryName, nexusComponentDs, formatMessage, activeTabKey, enableFlag }), [repositoryId, nexusComponentDs, formatMessage, activeTabKey, repositoryName]);
  const publishAuthProps = useMemo(() => ({ repositoryId, publishAuthDs, formatMessage, activeTabKey, enableFlag }), [publishAuthDs, formatMessage, activeTabKey, repositoryId]);
  const optLogProps = useMemo(() => ({ repositoryId, mavenOptLogDs, formatMessage, activeTabKey }), [mavenOptLogDs, formatMessage, activeTabKey, repositoryId]);

  const addMemberButtonProps = useMemo(() => ({ repositoryId, publishAuthDs, formatMessage }), [repositoryId, publishAuthDs, formatMessage]);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      overViewDs.query();
    } else if (activeTabKey === TabKeyEnum.NEXUS_COMPONENT) {
      nexusComponentDs.query();
    } else if (activeTabKey === TabKeyEnum.PUBLIST_AUTH) {
      publishAuthDs.query();
    } else {
      mavenOptLogDs.query();
    }
  };

  return (
    <React.Fragment>
      <Header>
        <MavenGuideButton {...guideButtonProps} />
        {activeTabKey === TabKeyEnum.NEXUS_COMPONENT && type === 'hosted' && versionPolicy === 'RELEASE' && enableFlag === 'Y' &&
          <MavenUploadButton {...uploadPackageButtonProps} />
        }
        {activeTabKey === TabKeyEnum.PUBLIST_AUTH && enableFlag === 'Y' && <MavenAddMemberButton {...addMemberButtonProps} />}
        <Button
          icon="refresh"
          onClick={() => refresh()}
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
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.overviewRepo`, defaultMessage: '仓库总览' })} key={TabKeyEnum.OVERVIEW}>
            <OverView {...overViewProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.nexusComponent`, defaultMessage: '包列表' })} key={TabKeyEnum.NEXUS_COMPONENT}>
            <NexusComponent {...nexusComponentProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.publishAuth`, defaultMessage: '发布权限' })} key={TabKeyEnum.PUBLIST_AUTH}>
            <PublishAuth {...publishAuthProps} />
          </TabPane>
          {ownerPermission &&
            <TabPane tab={formatMessage({ id: `${intlPrefix}.view.optLog`, defaultMessage: '操作日志' })} key={TabKeyEnum.OPTLOG}>
              <OptLog {...optLogProps} />
            </TabPane>
          }
        </Tabs>
      </Content>
    </React.Fragment >
  );
};

export default observer(MavenTabContainer);
