/**
* 镜像描述
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback, useMemo } from 'react';
import { message } from 'choerodon-ui';
import { Table, Modal, Form, TextField, Spin, Tooltip } from 'choerodon-ui/pro';
import { Action, axios } from '@choerodon/boot';
import { observer, useLocalStore } from 'mobx-react-lite';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import moment from 'moment';
import { get, map, forEach } from 'lodash';
import { TimePopover } from '@choerodon/components';
import { StatusTag } from '@choerodon/components';
import PullGuideModal from './PullGuideModal';
import BuildLogModal from './BuildLogModal';
import ScanReprot from './ScanReportModal';

const intlPrefix = 'infra.prod.lib';
const { Column } = Table;

const intervals = [];

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  flexShrink: 0,
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
  flexShrink: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};

const TagModal = ({ dockerImageTagDs, dockerImageScanDetailsDs, formatMessage, repoName, imageName, userAuth, modal, projectId }) => {
  async function init() {
    try {
      dockerImageTagDs.setQueryParameter('repoName', repoName);
      const res = await dockerImageTagDs.query();
      if (res && res.failed) {
        return false;
      }
      return true;
    } catch (error) {
      throw new Error(error);
    }
  }

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
    ['FINISHED', { code: 'success', name: '已完成' }],
    ['RUNNING', { code: 'running', name: '扫描中' }],
    ['SCANNING', { code: 'running', name: '扫描中' }],

    ['PENDING', { code: 'pending', name: '准备中' }],
    ['QUEUED', { code: 'pending', name: '准备中' }],
    ['SCHEDULED', { code: 'pending', name: '准备中' }],

    ['ERROR', { code: 'failed', name: '失败' }],
    ['STOPPED', { code: 'unready', name: '未扫描' }],
    ['UNKNOWN', { code: 'unready', name: '未扫描' }],
  ]), []);

  const rendererTag = ({ text }) => {
    const { code, name } = statusMap.get(text.toUpperCase()) || {};
    return <StatusTag colorCode={code} type="border" name={name} />;
  };

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

  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(info) {
      this.info = info;
    },
  }));

  const buildInfo = useLocalStore(() => ({
    info: '',
    setBuildInfo(info) {
      this.info = info;
    },
  }));

  const fetchGuide = useCallback(async ({ tagName }) => {
    try {
      const res = await axios.get(`/rdupm/v1/harbor-guide/tag?digest=${tagName}&repoName=${repoName}`);
      guideInfo.setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, []);

  const fetchBuildLog = useCallback(async (data) => {
    const { tagName, digest } = data;
    try {
      const res = await axios.get(`/rdupm/v1/harbor-image-tag/build/log?tagName=${tagName}&repoName=${repoName}&digest=${digest}`);
      buildInfo.setBuildInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, []);

  const handleOpenGuideModal = useCallback(async ({ tagName, type }) => {
    fetchGuide({ tagName });
    const key = Modal.key();
    Modal.open({
      key,
      title: type ? '版本拉取' : '摘要拉取',
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <PullGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchGuide, guideInfo]);

  const handleOpenLogModal = useCallback(async (data) => {
    await fetchBuildLog(data);
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      className: 'product-lib-docker-img-tag-buildlog-modal',
      children: <BuildLogModal formatMessage={formatMessage} buildInfo={buildInfo} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchBuildLog, buildInfo]);

  const handleDelete = async (data) => {
    const { tagName } = data;
    const button = await Modal.confirm({
      children: (
        <p>
          {`确认删除镜像 ${imageName} ${tagName}? 如果您删除此 Tag，则这个 Tag 引用的同一个 digest 的所有其他 Tag 也将被删除`}
        </p>
      ),
    });
    if (button !== 'cancel') {
      try {
        await axios.delete(`/rdupm/v1/harbor-image-tag/project/${projectId}}/delete?tagName=${tagName}&repoName=${repoName}`);
        message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
        dockerImageTagDs.query();
      } catch (error) {
        // message.error(error);
      }
    }
  };

  const handleScanReport = ({ digest, tagName }) => {
    Modal.open({
      key: Modal.key(),
      title: '漏洞扫描详情',
      children: <ScanReprot digest={digest} tagName={tagName} repoName={repoName} dockerImageScanDetailsDs={dockerImageScanDetailsDs} rendererTag={rendererTag} />,
      drawer: true,
      okCancel: false,
      okText: '关闭',
      style: {
        width: '7.4rem',
      },
    });
  };

  function handleFailedLink({ logUrl }) {
    window.open(logUrl);
  }

  const renderAction = useCallback(({ record }) => {
    const data = record.toData();
    const {
      digest,
      tagName,
      scanOverview,
    } = data;
    let actionData = [];
    const scanStatus = get(scanOverview, 'scanStatus').toUpperCase && get(scanOverview, 'scanStatus').toUpperCase();
    const logUrl = get(scanOverview, 'logUrl');
    if (userAuth?.includes('projectAdmin')) {
      actionData = [
        {
          service: [],
          text: '摘要拉取',
          action: () => handleOpenGuideModal({ tagName: digest }),
        },
        {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
          action: () => handleOpenLogModal(data),
        },
        {
          service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
          text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
          action: () => handleDelete(data),
        },
      ];
    } else {
      actionData = [
        {
          service: [],
          text: '摘要拉取',
          action: () => handleOpenGuideModal({ tagName: digest }),
        },
        {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
          action: () => handleOpenLogModal(data),
        },
      ];
    }
    if (['FINISHED', 'SUCCESS'].includes(scanStatus)) {
      actionData.push({
        service: [],
        text: formatMessage({ id: `${intlPrefix}.view.scanningReport`, defaultMessage: '漏洞扫描详情' }),
        action: () => handleScanReport({ digest, tagName }),
      }); 
    }
    if (scanStatus === 'FAILED') {
      actionData.push({
        service: [],
        text: '查看失败日志',
        action: () => handleFailedLink({ logUrl }),
      });
    }
    return <Action data={actionData} />;
  }, []);

  const rendererIcon = (imageUrl, text, loginName) => {
    let iconElement;
    if (imageUrl) {
      iconElement = <img style={imgStyle} src={imageUrl} alt="" />;
    } else {
      iconElement = <div style={iconStyle}>{get(text, 'length') && text[0]}</div>;
    }
    return (
      <Tooltip title={`${text}（${loginName}）`}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {iconElement}
          <span
            style={{
              marginLeft: '7px',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {`${text}（${loginName}）`}
          </span>
        </div>
      </Tooltip>
    );
  };

  const renderExpand = ({ record }) => {
    const versions = record.get('tags');
    return (
      <div className="product-lib-docker-taglist-subTableContainer">
        <span className="product-lib-docker-taglist-line" />
        <table className="product-lib-docker-taglist-subTable">
          <tr className="product-lib-docker-taglist-subTable-header">
            <th>版本号</th>
            <th />
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
                <td><Action data={
                  [{
                    service: [],
                    text: '版本拉取',
                    action: () => handleOpenGuideModal({ tagName: get(item, 'name'), type: 'version' }),
                  }]
                }
                />
                </td>
                <td><TimePopover content={get(item, 'pushTime')} /></td>
                <td><TimePopover content={get(item, 'pullTime')} /></td>
                <td>
                  {
                    rendererIcon(get(item, 'userImageUrl'), get(item, 'realName'), get(item, 'loginName'))
                  }
                </td>
              </tr>
            ))
          }
        </table>
      </div>
    );
  };

  async function handleScanning() {
    const scanData = map(dockerImageTagDs.currentSelected, (record) => ({
      repoName,
      tagName: record.get('tagName'),
      digest: record.get('digest'),
    }));
    try {
      const res = await axios.post(`/rdupm/v1/harbor-image/project/${projectId}/scan-images`, JSON.stringify(scanData));
      if (res && res.failed) {
        message.error(res.message);
        return false;
      }
      return true;
    } catch (error) {
      throw new Error(error);
    }
  }

  const renderScanStatusTag = useCallback(({ record }) => {
    if (!record.get('scanOverview')) {
      return <StatusTag colorCode="unready" name="未扫描" />;
    }
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

  async function handleGetStatus(record, interval) {
    const tempObj = {
      digest: record.get('digest'),
      repoName,
      tagName: record.get('tagName'),
    };
    try {
      const res = await axios.post(`/rdupm/v1/harbor-image/project/${projectId}/scan-images-result`, JSON.stringify(tempObj));
      if (res && res.failed) {
        message.error(res.message);
        clearInterval(interval);
        record.selectable = true;
      }
      const hasScanOverview = get(res, 'scanOverview');
      if (hasScanOverview && get(hasScanOverview, 'scanStatus').toUpperCase && !['RUNNING', 'SCANNING', 'PENDING', 'QUEUED', 'SCHEDULED'].includes(get(hasScanOverview, 'scanStatus').toUpperCase())) {
        clearInterval(interval);
        record.set(res);
        record.selectable = true;
        return;
      }
    } catch (error) {
      clearInterval(interval);
      record.selectable = true;
      throw new Error(error);
    }
  }

  async function handletest() {
    try {
      const beginScan = await handleScanning();
      if (beginScan) {
        forEach(dockerImageTagDs.currentSelected, (record) => {
          record.selectable = false;
          record.isSelected = false;
          record.set('scanOverview', {
            ...record.get('scanOverview'),
            scanStatus: 'RUNNING',
          });
          const time = setInterval(() => {
            intervals.push(time);
            handleGetStatus(record, time);
          }, 2000);
        });
      }
    } catch (error) {
      throw new Error(error);
    }

    return false;
  }

  useEffect(() => {
    init();
    return () => {
      if (intervals.length) {
        forEach(intervals, (timmer) => {
          clearInterval(timmer);
        });
      }
    };
  }, []);

  useEffect(() => {
    const selectedRecords = dockerImageTagDs.currentSelected;
    const upDateProps = {
      okProps: {
        disabled: !get(selectedRecords, 'length'),
      },
    };
    modal.update(upDateProps);
  }, [dockerImageTagDs.currentSelected]);

  modal.handleOk(handletest);

  return (
    <React.Fragment>
      <div
        className="product-lib-docker-taglist-search"
        onKeyDown={(event) => {
          if (event.keyCode === 13) {
            dockerImageTagDs.query();
          }
        }}
      >
        <Form dataSet={dockerImageTagDs.queryDataSet}>
          <TextField name="tagName" />
        </Form>
      </div>
      <div 
        style={{
          background: '#F3F6FE',
          borderRadius: '5px',
          width: '100%',
          padding: '14px 16px',
          color: '#0F1358',
          marginBottom: '10px',
        }}
      >
        <p>执行扫描操作前，请确保该仓库已安装扫描插件。</p>
        <p style={{
          margin: '0',
        }}
        >请先勾选列表中的摘要，才能点击下方的扫描按钮
        </p>
      </div>
      <Table
        dataSet={dockerImageTagDs} 
        queryBar="none"
        mode="tree"
        className="product-lib-docker-taglist-table"
        expandedRowRenderer={renderExpand}
      >
        <Column
          name="digest"
          renderer={({ text }) => (
            <Tooltip title={text} placement="top" overlayClassName="product-lib-docker-image-tag-digest">
              <div className="product-lib-docker-image-tag-digest-text">{text}</div>
            </Tooltip>
          )}
        />
        <Column renderer={renderAction} width={60} />
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
        {/* <Column
          name="realName"
          renderer={({ text, record }) => {
            const { userImageUrl, loginName } = record.toData();
            return rendererIcon(userImageUrl, text, loginName);
          }}
        /> */}
        <Column
          width={100}
          name="pushTime"
          renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />}
        />
        <Column width={100} name="pullTime" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
      </Table>
    </React.Fragment>
  );
};

export default observer(TagModal);
