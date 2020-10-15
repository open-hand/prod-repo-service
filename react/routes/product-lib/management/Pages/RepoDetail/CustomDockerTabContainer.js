import React, { useMemo, useContext, useState, useRef } from 'react';
import { Header, Content, Breadcrumb } from '@choerodon/boot';
import { Button, Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { useStore } from '../../index';
import { RepositoryIdContext } from './../index';
import { OverView } from './CustomDockerTabs';

const { TabPane } = Tabs;

export const TabKeyEnum = {
  OVERVIEW: 'overview',
  ASSOCIATE: 'associate',
};


const DockerTabContainer = () => {
  const overviewRef = useRef();
  const { repoName, repoId } = useContext(RepositoryIdContext);
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.OVERVIEW);

  const {
    intlPrefix,
    intl: { formatMessage },
  } = useStore();

  const overViewProps = useMemo(() => ({ repositoryId: repoId, formatMessage, activeTabKey }), [formatMessage, activeTabKey, repoId]);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      overviewRef.current.init(repoId);
    }
  };

  return (
    <React.Fragment>
      <Header>
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
        </Tabs>
      </Content>
    </React.Fragment >
  );
};

export default observer(DockerTabContainer);
