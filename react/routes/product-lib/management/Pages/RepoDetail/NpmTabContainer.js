/* eslint-disable */
/**
 * 制品库项目层
 * @author JZH <zhihao.jiang@hand-china.com>
 * @creationDate 2020/3/31
 * @copyright 2020 ® HAND
 */
import React, { useMemo, useContext, useState } from 'react';
import { Header, Content, Breadcrumb } from '@choerodon/boot';
import { Tabs } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { useCheckPermission } from '@/utils';
import { useStore } from '../../index';
import { RepositoryIdContext } from '../index';
import {
  OverView, PackageList, PublishAuth, OptLog,
} from './NpmTabs';
import { NpmGuideButton } from '../GuideButton';
import { NpmUploadButton } from '../UploadPackageButton';
import { MavenAddMemberButton } from '../AddAuthButton';
import { HeaderButtons } from '@choerodon/master';

const { TabPane } = Tabs;

export const TabKeyEnum = {
  OVERVIEW: 'overview',
  PACKAGE_LIST: 'packageList',
  PUBLIST_AUTH: 'publishAuth',
  OPTLOG: 'optLog',
};

const NpmTabContainer = (props) => {
  const {
    repositoryId, repositoryName, name, type, enableFlag,
  } = useContext(RepositoryIdContext);
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.OVERVIEW);
  const {
    intlPrefix,
    intl: { formatMessage },
    npmOverViewDs,
    npmComponentDs,
    publishAuthDs,
    npmOptLogDs,
  } = useStore();
  const ownerPermission = useCheckPermission(['choerodon.code.project.infra.product-lib.ps.project-owner-npm']);

  const uploadPackageButtonProps = useMemo(() => ({
    repositoryId, repositoryName, formatMessage, npmComponentDs,
  }), [repositoryId, repositoryName, npmComponentDs, formatMessage]);
  const guideButtonProps = useMemo(() => ({ npmOverViewDs, formatMessage }), [npmOverViewDs, formatMessage]);

  const overViewProps = useMemo(() => ({
    repositoryId, npmOverViewDs, formatMessage, activeTabKey,
  }), [npmOverViewDs, formatMessage, activeTabKey, repositoryId]);
  const packageListComponentProps = useMemo(() => ({
    repositoryId, npmOverViewDs, repositoryName, npmComponentDs, formatMessage, activeTabKey, enableFlag,
  }), [repositoryId, npmOverViewDs, npmComponentDs, formatMessage, activeTabKey, repositoryName]);
  const publishAuthProps = useMemo(() => ({
    repositoryId, publishAuthDs, formatMessage, activeTabKey, enableFlag,
  }), [publishAuthDs, formatMessage, activeTabKey, repositoryId]);
  const optLogProps = useMemo(() => ({
    repositoryId, npmOptLogDs, formatMessage, activeTabKey,
  }), [npmOptLogDs, formatMessage, activeTabKey, repositoryId]);

  const addMemberButtonProps = useMemo(() => ({ repositoryId, publishAuthDs, formatMessage }), [repositoryId, publishAuthDs, formatMessage]);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      npmOverViewDs.query();
    } else if (activeTabKey === TabKeyEnum.PACKAGE_LIST) {
      npmComponentDs.query();
    } else if (activeTabKey === TabKeyEnum.PUBLIST_AUTH) {
      publishAuthDs.query();
    } else {
      npmOptLogDs.query();
    }
  };

  debugger

  return (
    <>
      <Header>
        <HeaderButtons 
          items={[
            {
              display: activeTabKey === TabKeyEnum.PUBLIST_AUTH && enableFlag === 'Y',
              element: <MavenAddMemberButton {...addMemberButtonProps} activeRepository={props.activeRepository} />,
            },
            {
              display: activeTabKey === TabKeyEnum.PACKAGE_LIST && type === 'hosted' && enableFlag === 'Y',
              element: <NpmUploadButton {...uploadPackageButtonProps} />,
            },
            {
              element: <NpmGuideButton {...guideButtonProps} />
            },
            {
              iconOnly: true,
              icon: 'refresh',
              name: formatMessage({ id: 'refresh' }),
              handler: refresh,
              color: 'default'
            }
          ]}
        />
      </Header>

      <Breadcrumb title={name} />

      <Content className="product-lib-management">
        <Tabs
          activeKey={activeTabKey}
          animated={false}
          onChange={(newActiveKey) => setActiveTabKey(newActiveKey)}
          className="product-lib-management-tabs"
        >
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.overviewRepo`, defaultMessage: '仓库总览' })} key={TabKeyEnum.OVERVIEW}>
            <OverView {...overViewProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.nexusComponent`, defaultMessage: '包列表' })} key={TabKeyEnum.PACKAGE_LIST}>
            <PackageList {...packageListComponentProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.userAuth`, defaultMessage: '用户权限' })} key={TabKeyEnum.PUBLIST_AUTH}>
            <PublishAuth
              {...publishAuthProps}
              activeRepository={props.activeRepository}
            />
          </TabPane>
          {ownerPermission
            && (
            <TabPane tab={formatMessage({ id: `${intlPrefix}.view.optLog`, defaultMessage: '操作日志' })} key={TabKeyEnum.OPTLOG}>
              <OptLog {...optLogProps} activeRepository={props.activeRepository} />
            </TabPane>
            )}
        </Tabs>
      </Content>
    </>
  );
};

export default observer(NpmTabContainer);
