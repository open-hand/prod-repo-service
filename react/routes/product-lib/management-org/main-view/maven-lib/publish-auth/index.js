/**
* 制品库用户权限
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/7
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Table } from 'choerodon-ui/pro';
// import { Tag } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import renderFullName from '@/utils/renderer';
import { useMavenStore } from '../stores';
import { useProdStore } from '../../../stores';
import './index.less';


const { Column } = Table;
const PublishAuth = () => {
  const { prodStore: { getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      AUTH_TAB,
    },
    mavenStore,
    publishAuthDs,
  } = useMavenStore();
  const { getTabKey } = mavenStore;
  useEffect(() => {
    if (getTabKey === AUTH_TAB) {
      publishAuthDs.query();
    }
  }, [getTabKey, getSelectedMenu]);

  // const rendererTags = ({ text }) => {
  //   const aTags = text.split(',').filter(Boolean);
  //   return aTags.length !== 0 && aTags.map(o => (
  //     <Tag key={o} className="product-lib-org-publishAuth-tags">
  //       {o}
  //     </Tag>
  //   ));
  // };

  return (
    <div style={{ overflowX: 'hidden', height: 'calc(100% - 75px)' }}>
      <Table dataSet={publishAuthDs} className="no-border-top-table" >
        {/* <Column name="neUserId" /> */}
        <Column name="neRepositoryName" width={150} renderer={renderFullName} />
        <Column
          name="projectName"
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  loginName: record.get('projectId'),
                  realName: record.get('projectName'),
                  imageUrl: record.get('projectImgUrl'), // TODO
                }}
              />
            </div>
          )}
        />
        {/* <Column name="otherRepositoryName" renderer={rendererTags} />
        <Column name="code" />
        <Column name="name" /> */}
        <Column name="loginName" />
        <Column
          name="realName"
          sortable
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  loginName: record.get('loginName'),
                  realName: record.get('realName'),
                  imageUrl: record.get('userImageUrl'),
                }}
              />
            </div>
          )}
        />
        <Column name="memberRole" />
        <Column name="roleCode" />
        <Column name="endDate" renderer={renderFullName} />
      </Table>
    </div>
  );
};

export default observer(PublishAuth);
