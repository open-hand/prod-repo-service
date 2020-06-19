/**
* 制品库包列表
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Table, Button, Form, TextField, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import UserAvatar from '@/components/user-avatar';
import TimePopover from '@/components/time-popover/TimePopover';
import { useMavenStore } from '../stores';
import { useProdStore } from '../../../stores';
import GuideButton from './guide-button';
import './index.less';


const { Column } = Table;
const PackageList = () => {
  const { prodStore: { getRepositoryId, getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      PACKAGE_TAB,
    },
    mavenStore,
    intl: { formatMessage },
    packageListDs,
  } = useMavenStore();
  const { getTabKey } = mavenStore;

  useEffect(() => {
    if (getTabKey === PACKAGE_TAB) {
      packageListDs.queryDataSet.records[0].set('repositoryId', getRepositoryId);
      if (getRepositoryId) {
        packageListDs.query();
      }
    }
  }, [getTabKey, getSelectedMenu, getRepositoryId]);

  const handleSearch = () => {
    packageListDs.query();
  };

  const renderQueryBar = ({ queryDataSet }) => (
    <div
      style={{ display: 'flex', alignItems: 'center' }}
    >
      <Form dataSet={queryDataSet} columns={4} className="product-lib-org-nexusComponent-queryform">
        <Select
          name="repositoryId"
          onChange={handleSearch}
          searchable
          clearButton
        />
        <TextField name="version" onChange={handleSearch} />
        <TextField name="group" onChange={handleSearch} />
        <TextField name="name" onChange={handleSearch} />
      </Form >
      <Button
        className="product-lib-org-nexusComponent-reset-button"
        funcType="raised"
        onClick={() => queryDataSet.current.reset()}
      >
        {formatMessage({ id: 'reset', defaultMessage: '重置' })}
      </Button>
    </div>
  );

  const rendererDropDown = ({ text, record }) => (
    <div className="product-lib-org-management-lib-list-render-dropdown-column">
      <GuideButton text={text} record={record} repositoryId={packageListDs.queryDataSet.records[0].get('repositoryId')} formatMessage={formatMessage} />
    </div>
  );

  function renderUserName(record) {
    const avatar = (
      <UserAvatar
        user={{
          // id: record.get('user').userId,
          loginName: record.creatorLoginName,
          realName: record.creatorRealName,
          imageUrl: record.creatorImageUrl,
        }}
        style={{ maxWidth: '2.5rem' }}
      // hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }
  function renderTime(value) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }
  function renderUserName1({ record }) {
    const avatar = (
      <UserAvatar
        user={{
          // id: record.get('user').userId,
          loginName: record.get('creatorLoginName'),
          realName: record.get('creatorRealName'),
          imageUrl: record.get('creatorImageUrl'),
        }}
        style={{ maxWidth: '2.5rem' }}
      // hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }
  function renderTime1({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  const expandedRowRenderer = ({ record }) => {
    const { components } = record.toData();
    return (
      <table style={{ width: '100%' }}>
        <tbody>
          {
            components.map(o => (
              <tr key={o.id}>
                <td style={{ paddingLeft: '10px' }}>{rendererDropDown({ text: o.version, record: o })}</td>
                <td style={{ paddingLeft: '10px' }}>{o.group}</td>
                <td style={{ paddingLeft: '20px' }}>{o.name}</td>
                <td style={{ paddingLeft: '10px' }}>{renderUserName(o)}</td>
                <td style={{ paddingLeft: '20px' }}>{renderTime(o.creationDate)}</td>
              </tr>
            ))
          }
        </tbody>
      </table>
    );
  };

  return (
    <div style={{ overflowX: 'hidden', height: 'calc(100% - 75px)' }}>
      <Table
        dataSet={packageListDs}
        queryBar={renderQueryBar}
        expandedRowRenderer={expandedRowRenderer}
      >
        <Column name="version" renderer={({ text, record }) => rendererDropDown({ text, record: record.toData() })} />
        <Column name="group" />
        <Column name="name" />
        <Column name="creatorRealName" renderer={renderUserName1} width={200} />
        <Column name="creationDate" renderer={renderTime1} />
      </Table>
    </div>
  );
};

export default observer(PackageList);
