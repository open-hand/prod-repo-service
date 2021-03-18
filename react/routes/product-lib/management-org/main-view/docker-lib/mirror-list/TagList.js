/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect, useCallback } from 'react';
import { Action, axios } from '@choerodon/boot';
import { Tooltip } from 'choerodon-ui';
import { Table, Modal, Form, TextField, Icon, DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import TimePopover from '@/components/time-popover/TimePopover';
import UserAvatar from '@/components/user-avatar';
import TagGuide from '../modals/tag-guide/GuideModal';
import TagPullDS from '../stores/TagPullDS';
import BuildLog from '../modals/build-log';
import VersionCopy from '../modals/version-copy';

import './index.less';


const { Column } = Table;

const modalKey = Modal.key();

const TagList = observer(({ mirrorListDS, dataSet, repoName, intlPrefix, formatMessage, organizationId, repoListDs }) => {
  function refresh() {
    dataSet.query();
  }
  function refreshMirrorList() {
    mirrorListDS.query();
  }

  useEffect(() => {
    refresh();
  }, []);

  // 版本拉取
  function openGuideModal(record) {
    const tagPullDs = new DataSet(TagPullDS({ intlPrefix, formatMessage, tagName: record.get('tagName'), organizationId, repoName }));
    const guidePros = {
      formatMessage,
      tagPullDs,
      intlPrefix,
      organizationId,
    };
    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.pullImageByTag`, defaultMessage: '版本拉取' }),
      style: {
        width: '7.4rem',
      },
      children: <TagGuide {...guidePros} />,
      footer: (okBtn) => okBtn,
      okText: formatMessage({ id: 'close' }),
    });
  }

  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  // 删除
  // function handleDelete() {
  //   const record = dataSet.current;
  //   const mProps = {
  //     title: formatMessage({ id: 'confirm.delete' }),
  //     // children: formatMessage({ id: `${intlPrefix}.permission.delete.des` }),
  //     children: (
  //       <p>
  //         {`确认删除镜像 ${repoName} ${record.get('tagName')}? 如果您删除此 Tag，则这个 Tag 引用的同一个 digest 的所有其他 Tag 也将被删除`}
  //       </p>
  //     ),
  //     okText: formatMessage({ id: 'delete' }),
  //     okProps: { color: 'red' },
  //     cancelProps: { color: 'dark' },
  //   };
  //   dataSet.delete(record, mProps);
  // }

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

  // // 构建日志
  // function openLogModal(record) {
  //   Modal.open({
  //     title: formatMessage({ id: 'view.log' }),
  //     key: Modal.key(),
  //     style: {
  //       width: '7.4rem',
  //     },
  //     children: <BuildLog repoName={repoName} tagName={record.get('tagName')} />,
  //     drawer: true,
  //     okText: formatMessage({ id: 'close' }),
  //     footer: (okbtn) => (
  //       <React.Fragment>
  //         {okbtn}
  //       </React.Fragment>
  //     ),
  //   });
  // }

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

  function renderAction({ record }) {
    const actionData = [
      // {
      //   service: [],
      //   text: formatMessage({ id: 'delete' }),
      //   action: handleDelete,
      // },
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
    return <Action data={actionData} />;
  }

  function handleTableFilter(record) {
    return record.status !== 'add';
  }


  function renderName({ text, record }) {
    return (
      <Tooltip title={text} mouseEnterDelay={0.5} placement="top" overlayClassName="product-lib-org-management-docker-image-tag-digest">
        <span className="product-lib-org-management-mirror-list-guide-modal-click-table-name" onClick={() => openGuideModal(record)}>{text}</span>
      </Tooltip>
    );
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

  function renderUserName({ record }) {
    const avatar = (
      <UserAvatar
        user={{
          // id: record.get('user').userId,
          loginName: record.get('loginName'),
          realName: record.get('realName'),
          imageUrl: record.get('userImageUrl'),
        }}
      // hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }

  return (
    <React.Fragment >
      {renderFilterForm()}
      <Table dataSet={dataSet} queryBar="none" filter={handleTableFilter}>
        <Column name="tagName" renderer={renderName} />
        <Column renderer={renderAction} width={70} />
        <Column name="sizeDesc" />
        <Column name="dockerVersion" />
        <Column name="os" renderer={({ record }) => `${record.get('os')}/${record.get('architecture')}`} />
        <Column
          name="digest"
          renderer={({ text }) =>
            (
              <Tooltip title={text} mouseEnterDelay={0.5} placement="top" overlayClassName="product-lib-org-management-docker-image-tag-digest">
                <div className="product-lib-org-management-docker-image-tag-digest-text">{text}</div>
              </Tooltip>
            )}
        />
        <Column name="author" renderer={renderUserName} width={150} />
        {/* <Column name="createTime" renderer={renderTime} /> */}
        <Column name="pushTime" renderer={renderTime} />
        <Column name="pullTime" renderer={renderTime} />
        {/* <Column
          name="createdByName"
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  id: record.get('createdUser').userId,
                  loginName: record.get('createdUser').loginName,
                  realName: record.get('createdUser').realName,
                  imageUrl: record.get('createdUser').imageUrl,
                  email: record.get('createdUser').email,
                }}
              // hiddenText
              />
            </div>
          )}
        /> */}
      </Table>
    </React.Fragment>
  );
});

export default TagList;
