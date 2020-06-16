/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from 'react';
import { Tooltip } from 'choerodon-ui';
import { Table, Modal, Form, TextField, Icon } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import UserAvatar from '@/components/user-avatar';
import TimePopover from '@/components/time-popover/TimePopover';
import TagGuide from '../modals/tag-guide/GuideModal';

import './index.less';


const { Column } = Table;

const modalKey = Modal.key();

const TagList = observer(({ dataSet, intlPrefix, formatMessage }) => {
  function refresh() {
    dataSet.query();
  }

  useEffect(() => {
    refresh();
  }, []);

  // 版本拉取
  function openGuideModal(record) {
    const guidePros = {
      formatMessage,
      name: record.get('name'),
      version: record.get('version'),
      repositoryUrl: record.get('repositoryUrl'),
      intlPrefix,
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

  function handleTableFilter(record) {
    return record.status !== 'add';
  }


  function renderName({ text, record }) {
    return <span className="product-lib-org-management-package-list-guide-modal-click-table-name" onClick={() => openGuideModal(record)}>{text}</span>;
  }


  function renderFilterForm() {
    return (
      <Form
        dataSet={dataSet.queryDataSet}
        labelLayout="float"
      >
        <TextField
          name="version"
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
  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  return (
    <React.Fragment >
      {renderFilterForm()}
      <Table dataSet={dataSet} queryBar="none" filter={handleTableFilter}>
        <Column name="version" renderer={renderName} width={150} />
        <Column name="creatorRealName" renderer={renderUserName} width={280} />
        <Column name="creationDate" renderer={renderTime} />
        <Column
          name="downloadUrl"
          renderer={({ text }) =>
            (
              <Tooltip title={text} mouseEnterDelay={0.5} placement="top" overlayClassName="product-lib-org-management-docker-image-tag-digest">
                <div className="product-lib-org-management-docker-image-tag-digest-text">{text}</div>
              </Tooltip>
            )}
        />
      </Table>
    </React.Fragment>
  );
});

export default TagList;
