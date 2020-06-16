import React, { useMemo, useContext, useState, useRef } from 'react';
import { Header, Content, Breadcrumb } from '@choerodon/boot';
import { Button, Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { useStore } from '../../index';
import { RepositoryIdContext } from './../index';
import { OverView, AssociatedAppSVC } from './CustomDockerTabs';
import DockerAssociateAppSvcBtn from '../DockerAssociateAppSvcBtn';

const { TabPane } = Tabs;

export const TabKeyEnum = {
  OVERVIEW: 'overview',
  ASSOCIATE: 'associate',
};


const DockerTabContainer = () => {
  const overviewRef = useRef();
  const associatedAppSVCRef = useRef();
  const { repoName, repoId, projectShare } = useContext(RepositoryIdContext);
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.OVERVIEW);
  const [detail, setDetailData] = useState({});

  const {
    intlPrefix,
    intl: { formatMessage },
  } = useStore();

  const overViewProps = useMemo(() => ({ setDetailData, repositoryId: repoId, formatMessage, activeTabKey }), [formatMessage, activeTabKey, repoId]);
  const associatedAppSVCProps = useMemo(() => ({ detail, repositoryId: repoId, projectShare, formatMessage, activeTabKey }), [detail, repoId, projectShare, formatMessage, activeTabKey]);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      overviewRef.current.init(repoId);
    }
    if (activeTabKey === TabKeyEnum.ASSOCIATE) {
      associatedAppSVCRef.current.init();
    }
  };

  return (
    <React.Fragment>
      <Header>
        {activeTabKey === TabKeyEnum.ASSOCIATE && projectShare === 'false' &&
          <DockerAssociateAppSvcBtn repositoryId={repoId} formatMessage={formatMessage} refresh={refresh} />
        }
        <Button
          icon="refresh"
          onClick={refresh}
        >
          {formatMessage({ id: 'refresh' })}
        </Button>
      </Header>

      <Breadcrumb title={repoName} />

      <Content className="product-lib-management">
        <Tabs
          activeKey={activeTabKey}
          animated={false}
          onChange={newActiveKey => setActiveTabKey(newActiveKey)}
          className="product-lib-management-tabs"
        >
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.overviewRepo`, defaultMessage: '仓库总览' })} key={TabKeyEnum.OVERVIEW}>
            <OverView ref={overviewRef} {...overViewProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.associatedAppSVC`, defaultMessage: '关联应用服务' })} key={TabKeyEnum.ASSOCIATE}>
            <AssociatedAppSVC ref={associatedAppSVCRef} {...associatedAppSVCProps} />
          </TabPane>
        </Tabs>
      </Content>
    </React.Fragment >
  );
};

export default observer(DockerTabContainer);
