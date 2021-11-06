/**
* 制品库自建或关联仓库查询
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
// import { Content } from '@choerodon/boot';
import { Spin, Form, Output } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { TabKeyEnum } from '../../MavenTabContainer';
import './index.less';

const OverView = ({
  repositoryId, npmOverViewDs, formatMessage, activeTabKey,
}) => {
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      npmOverViewDs.repositoryId = repositoryId;
      npmOverViewDs.query();
    }
  }, [activeTabKey]);

  return (
    <div className="product-lib-overview">
      <Spin dataSet={npmOverViewDs}>
        <Form dataSet={npmOverViewDs} labelLayout="horizontal" labelWidth={165} labelAlign="left">
          <Output name="name" />
          <Output name="type" />
          {/* <Output name="versionPolicy" /> */}
          <Output
            name="online"
            renderer={({ value }) => (value ? formatMessage({ id: 'yes', defaultMessage: '是' }) : formatMessage({ id: 'no', defaultMessage: '否' }))}
          />
          <Output name="writePolicy" />
          <Output
            name="allowAnonymous"
            renderer={({ value }) => (value ? formatMessage({ id: 'yes', defaultMessage: '是' }) : formatMessage({ id: 'no', defaultMessage: '否' }))}
          />
          {npmOverViewDs.current?.get('url') && <Output name="url" renderer={({ value }) => <a href={value}>{value}</a>} />}
          {npmOverViewDs.current && npmOverViewDs.current.get('type') === 'proxy'
            && <Output name="remoteUrl" renderer={({ value }) => <a href={value}>{value}</a>} />}
          {npmOverViewDs.current && npmOverViewDs.current.get('type') === 'group'
            && <Output name="repoMemberList" />}
        </Form>
      </Spin>
    </div>

  );
};

export default observer(OverView);
