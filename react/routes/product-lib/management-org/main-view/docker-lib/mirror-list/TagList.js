/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect, useCallback, useMemo } from 'react';
import { Action, axios } from '@choerodon/boot';
import { Table, Modal, Form, TextField, Icon, Tooltip, Spin, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { isNil, get, forEach, map } from 'lodash';
import TimePopover from '@/components/time-popover/TimePopover';
import UserAvatar from '@/components/user-avatar';
import { StatusTag } from '@choerodon/components';
import BuildLog from '../modals/build-log';
import ScanReprot from './ScanReportModal';
import VersionCopy from '../modals/version-copy';

import './index.less';

const { Column } = Table;

const modalKey = Modal.key();

const TagList = observer(({ mirrorListDS, scanDetailDs, dataSet, repoName, intlPrefix, formatMessage, organizationId, repoListDs, modal }) => {
  function refresh() {
    dataSet.query();
  }
  function refreshMirrorList() {
    mirrorListDS.query();
  }

  useEffect(() => {
    refresh();
  }, []);

  const statusMap = useMemo(() => new Map([
    ['UNKNOWN', { code: 'unready', name: '未知' }],
    ['NEGLIGIBLE', { code: 'unready', name: '可忽略' }],
    ['LOW', { code: 'running', name: '较低' }],
    ['MEDIUM', { code: 'opened', name: '中等' }],
    ['HIGH', { code: 'error', name: '严重' }],
    ['CRITICAL', { code: 'disconnect', name: '危急' }],
  ]), []);

  const scanStatusMap = useMemo(() => new Map([
    ['SUCCESS', { code: 'success', name: '已完成' }],
    ['RUNNING', { code: 'running', name: '扫描中' }],
    ['PENDING', { code: 'pending', name: '准备中' }],
    ['FAILED', { code: 'failed', name: '失败' }],
  ]), []);

  const rendererTag = ({ text }) => {
    const { code, name } = statusMap.get(text.toUpperCase()) || {};
    return <StatusTag colorCode={code} type="border" name={name} />;
  };

  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  const fetchBuildLog = useCallback(async (data) => {
    const { tagName, digest } = data;
    try {
      const res = await axios.get(`/rdupm/v1/harbor-image-tag/build/log?repoName=${repoName}&tagName=${tagName}&digest=${digest}`);
      return res;
    } catch (error) {
      // message.error(error);
      return '';
    }
  }, []);

  // 构建日志
  const handleOpenLogModal = useCallback(async (record) => {
    const data = record.toData();
    const result = await fetchBuildLog(data);
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '7.4rem' },
      className: 'product-lib-org-docker-img-tag-buildlog-modal',
      children: <BuildLog formatMessage={formatMessage} buildInfo={result} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchBuildLog]);

  function openCopyModal(record) {
    Modal.open({
      title: formatMessage({ id: `${intlPrefix}.view.versionCopy` }),
      children: <VersionCopy
        refresh={refresh}
        refreshMirrorList={refreshMirrorList}
        intlPrefix={intlPrefix}
        srcRepoName={repoName}
        digest={record.get('digest')}
        repoListDs={repoListDs}
      />,
      key: modalKey,
      drawer: true,
      style: { width: '3.8rem' },
      destroyOnClose: true,
      className: 'base-lang-sider',
      okText: formatMessage({ id: 'ok' }),
    });
  }

  const handleScanReport = ({ digest, tagName }) => {
    Modal.open({
      key: Modal.key(),
      title: '漏洞扫描详情',
      children: <ScanReprot 
        digest={digest} 
        tagName={tagName} 
        repoName={repoName} 
        dockerImageScanDetailsDs={scanDetailDs} 
        rendererTag={rendererTag} 
      />,
      drawer: true,
      okCancel: false,
      okText: '关闭',
      style: {
        width: '7.4rem',
      },
    });
  };

  function renderAction({ record }) {
    const {
      digest,
      tagName,
      scanOverview,
    } = record.toData();
    const actionData = [
      {
        service: [],
        text: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
        action: () => handleOpenLogModal(record),
      },
      {
        service: [],
        text: formatMessage({ id: `${intlPrefix}.view.versionCopy`, defaultMessage: '版本复制' }),
        action: () => openCopyModal(record),
      },
    ];
    if (get(scanOverview, 'scanStatus').toUpperCase() === 'SUCCESS') {
      actionData.push({
        service: [],
        text: '漏洞扫描详情',
        action: () => handleScanReport({ digest, tagName }),
      });
    }
    return <Action data={actionData} />;
  }

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function renderFilterForm() {
    return (
      <Form
        dataSet={dataSet.queryDataSet}
        labelLayout="float"
      >
        <TextField
          name="tagName"
          placeholder={formatMessage({ id: `${intlPrefix}.view.enter.osVersion` })}
          prefix={<Icon type="search" />}
          onChange={refresh}
          style={{ width: '4rem' }}
        />
      </Form>
    );
  }

  function renderUserName(imageUrl, realName, loginName) {
    const avatar = (
      <UserAvatar
        user={{
          loginName,
          realName,
          imageUrl,
        }}
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }
  
  async function handleScanning() {
    const scanData = map(dataSet.currentSelected, (record) => ({
      repoName,
      tagName: record.get('tagName'),
      digest: record.get('digest'),
    }));
    try {
      const res = await axios.post(`/rdupm/v1/harbor-image/organization/${organizationId}/scan-images`, JSON.stringify(scanData));
      if (res && res.failed) {
        message.error(res.message);
        return false;
      }
      return true;
    } catch (error) {
      throw new Error(error);
    }
  }

  useEffect(() => {
    const selectedRecords = dataSet.currentSelected;
    const upDateProps = {
      okProps: {
        disabled: !get(selectedRecords, 'length'),
      },
    };
    modal.update(upDateProps);
  }, [dataSet.currentSelected]);

  const renderExpand = ({ record }) => {
    const versions = record.get('tags');
    return (
      <div className="product-lib-docker-taglist-subTableContainer">
        <span className="product-lib-docker-taglist-line" />
        <table className="product-lib-docker-taglist-subTable">
          <tr className="product-lib-docker-taglist-subTable-header">
            <th>版本号</th>
            <th>最近推送时间</th>
            <th>最近拉取时间</th>
            <th>推送者</th>
          </tr>
          {
            versions.map((item) => (
              <tr>
                <td>
                  <div className="product-lib-docker-taglist-subTable-dot"><span /><span />
                  </div>
                  {get(item, 'name')}
                </td>
                <td><TimePopover content={get(item, 'pushTime')} /></td>
                <td><TimePopover content={get(item, 'pullTime')} /></td>
                <td>
                  {
                    renderUserName(get(item, 'userImageUrl'), get(item, 'realName'), get(item, 'loginName'))
                  }
                </td>
              </tr>
            ))
          }
        </table>
      </div>
    );
  };

  const renderScanStatusTag = useCallback(({ record }) => {
    const text = get(record.get('scanOverview'), 'scanStatus');
    const {
      code,
      name,
    } = scanStatusMap.get(text.toUpperCase());
    const extraNode = (
      <Spin
        style={{
          height: '26px',
          width: '26px',
        }}
        display
        size="small"
      />
    );
    return (
      <div style={{
        display: 'flex',
        alignItems: 'center',
        height: '100%',
      }}
      >
        <StatusTag colorCode={code} name={name} />
        {text.toUpperCase() === 'RUNNING' ? extraNode : ''}
      </div>
    );
  }, []);

  const renderSeverityTag = ({ record }) => {
    const scanOverview = record.get('scanOverview') || {};
    const severity = get(scanOverview, 'severity');
    const fixable = get(scanOverview, 'fixable');
    const total = get(scanOverview, 'total');
    const summary = get(scanOverview, 'summary');
    const upperCode = severity && severity.toUpperCase();
    const statusName = upperCode === 'UNKNOWN' ? '无漏洞' : `总计${total} - 可修复${fixable}`;
    const tooltitle = (
      <div>
        <p>
          漏洞严重度：{get(statusMap.get(upperCode), 'name')}
        </p>
        <p>危急漏洞：{get(summary, 'critical')} </p>
        <p>严重漏洞：{get(summary, 'high')}</p>
        <p>中等漏洞：{get(summary, 'medium')}</p>
        <p>
          较低漏洞：{get(summary, 'low')}
        </p>
        <p>
          可忽略漏洞：{get(summary, 'negligible')}
        </p>
        <p>
          未知漏洞：{get(summary, 'unknown')}
        </p>
      </div>
    );
    return (
      upperCode ? (
        <Tooltip title={summary ? tooltitle : ''}>
          <div>
            <StatusTag type="border" colorCode={get(statusMap.get(upperCode), 'code')} name={statusName} />
          </div>
        </Tooltip>
      ) : '-'
    );
  };

  function handletest() {
    forEach(dataSet.currentSelected, (record) => {
      record.selectable = false;
      record.isSelected = false;
      record.set('scanOverview', {
        ...record.get('scanOverview'),
        severity: '',
        scanStatus: 'running',
      });
      setTimeout(() => {
        record.selectable = true;
        record.set('scanOverview', {
          ...record.get('scanOverview'),
          severity: 'High',
          scanStatus: 'success',
        });
      }, 1000);
    });
    return false;
  }

  modal.handleOk(handletest);

  return (
    <React.Fragment>
      {renderFilterForm()}
      <Table
        dataSet={dataSet}
        queryBar="none"
        mode="tree"
        filter={handleTableFilter}
        expandedRowRenderer={renderExpand}
        className="product-lib-docker-taglist-table"
      >
        <Column
          name="digest"
          renderer={({ text }) => (
            <Tooltip title={text} mouseEnterDelay={0.5} placement="top" overlayClassName="product-lib-org-management-docker-image-tag-digest">
              <div className="product-lib-org-management-docker-image-tag-digest-text">{text}</div>
            </Tooltip>
          )}
        />
        <Column renderer={renderAction} width={70} />
        <Column
          name="scanStatus"
          renderer={renderScanStatusTag}
          width={80}
        />
        <Column
          name="severity"
          renderer={renderSeverityTag}
        />
        <Column name="sizeDesc" />
        <Column name="os" renderer={({ record }) => `${record.get('os')}/${record.get('architecture')}`} />
        {/* <Column name="author" renderer={renderUserName} width={150} /> */}
        <Column name="pushTime" renderer={renderTime} />
        <Column name="pullTime" renderer={renderTime} />
      </Table>
    </React.Fragment>
  );
});

export default TagList;
