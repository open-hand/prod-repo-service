/* eslint-disable max-len */
/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import {
  Icon, Row, Col, message, Tooltip,
} from 'choerodon-ui';
import {
  Pagination, Spin, Modal, Form, Select, Button,
} from 'choerodon-ui/pro';
import { Choerodon, Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import { useDockerStore } from '../stores';
import { useProdStore } from '../../../stores';
import ResourceConfig from '../modals/resource-config';
import './index.less';

const { Option } = Select;

const MirrorLib = () => {
  const { prodStore: { setDockerRepoInfo, getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      MIRROR_TAB,
      LIST_TAB,
    },
    intlPrefix,
    dockerStore,
    intl: { formatMessage },
    mirrorLibDs,
  } = useDockerStore();
  const { setTabKey, getTabKey, updateAuth } = dockerStore;

  function refresh() {
    mirrorLibDs.query();
  }
  useEffect(() => {
    if (getTabKey === MIRROR_TAB) {
      refresh();
    }
  }, [getTabKey, getSelectedMenu]);
  const listData = mirrorLibDs.current && mirrorLibDs.toData();

  async function handleUpdateAuth({ item, publicFlag }) {
    const flag = publicFlag === 'true' ? 'false' : 'true';
    try {
      await updateAuth(item, flag)
        .then((res) => {
          if (res.failed) {
            throw res.message;
          } else {
            Choerodon.prompt(formatMessage({ id: 'success.operation' }));
            mirrorLibDs.query();
          }
        });
    } catch (error) {
      message.error(error);
    }
  }

  const openConfigModal = (code, projectId) => {
    const key = Modal.key();
    Modal.open({
      key,
      style: { width: '3.8rem' },
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.resourceConfig` }),
      className: 'infra-prod-lib-org-modals',
      children: <ResourceConfig
        refresh={refresh}
        intlPrefix={intlPrefix}
        repoName={code}
        projectId={projectId}
      />,
      okText: formatMessage({ id: 'save' }),
    });
  };

  const handleToPackage = (item) => {
    setTabKey(LIST_TAB);
    setDockerRepoInfo(item);
  };

  const renderer = ({ text, record }) => (
    <div style={{ width: '100%' }}>
      {text}
      {' '}
      {text && `(${record.get('code')})`}
    </div>
  );

  const optionRenderer = ({ text, record }) => (
    <div>
      {renderer({ text, record })}
    </div>
  );
  function renderFilterForm() {
    return (
      <Form
        dataSet={mirrorLibDs.queryDataSet}
        labelLayout="float"
        columns={9}
        className="product-lib-org-management-mirror-lib-filter-form"
      >
        <Select
          searchable
          clearButton
          name="code"
          onChange={refresh}
          optionRenderer={optionRenderer}
          renderer={renderer}
          colSpan={2}
        />
        <Select
          name="publicFlag"
          onChange={refresh}
          colSpan={2}
          searchable
          clearButton
        >
          <Option value="true">公开</Option>
          <Option value="false">不公开</Option>
        </Select>
        <div colSpan={5} style={{ width: '0.46rem', float: 'right' }}>
          <Button funcType="raised" type="reset" className="product-lib-org-management-mirror-lib-filter-form-btn">
            {formatMessage({ id: 'reset' })}
          </Button>
        </div>
      </Form>
    );
  }

  const renderAction = (item) => {
    const actionData = [{
      service: [],
      text: formatMessage({ id: `${intlPrefix}.view.resourceConfig` }),
      action: () => openConfigModal(item.code, item.projectId),
    },
    // {
    // service: [],
    // text: formatMessage({ id: 'delete' }),
    // action: () => openDelete(item),
    // },
    {
      service: [],
      text: formatMessage({ id: `${intlPrefix}.view.seeDetail` }),
      action: () => handleToPackage(item),
    }];
    return (
      <Action
        placement="bottomRight"
        data={actionData}
      />
    );
  };

  function renderData() {
    return listData ? (
      <ul>
        {
          listData.map((item) => {
            const {
              id, code, creatorLoginName, creatorRealName, creationDate, name, publicFlag, repoCount,
            } = item;
            return (
              <li key={id + code}>
                <div className="product-lib-org-management-mirror-lib-list-card">
                  <Row className="product-lib-org-management-mirror-lib-list-card-header">
                    <Col span={9} className="product-lib-org-management-mirror-lib-list-card-header-icon">
                      {/* {rendererOnlineStatus(online)} */}
                      <span
                        role="none"
                        className="product-lib-org-management-mirror-lib-list-card-header-title c7ncd-prolib-clickText"
                        onClick={() => handleToPackage(item)}
                      >
                        {code}
                      </span>
                      <Tooltip title={publicFlag === 'true' ? formatMessage({ id: `${intlPrefix}.view.public` }) : formatMessage({ id: `${intlPrefix}.view.private` })}>
                        <Icon type={publicFlag === 'true' ? 'unlock' : 'lock'} onClick={() => handleUpdateAuth({ item, publicFlag })} />
                      </Tooltip>
                    </Col>
                    <Col span={12} className="product-lib-org-management-mirror-lib-list-card-header-project">
                      <div style={{ display: 'inline-flex' }}>
                        <UserAvatar
                          user={{
                            loginName: name,
                            realName: name,
                            imageUrl: '', // TODO
                          }}
                          size="0.18rem"
                          hiddenText
                          showToolTip={false}
                        />
                      </div>
                      <span className="product-lib-org-management-mirror-lib-list-card-header-project-name">{name}</span>
                    </Col>
                  </Row>
                  <Row className="product-lib-org-management-mirror-lib-list-card-footer">
                    <Col span={9}>
                      <Icon type="account_circle-o" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.createdBy` })}
                        ：
                      </span>
                      <span className="product-lib-org-management-mirror-lib-list-card-footer-text">{creatorRealName ? `${creatorRealName} (${creatorLoginName})` : ''}</span>
                    </Col>
                    <Col span={9} className="product-lib-org-management-mirror-lib-list-card-header-project">
                      <Icon type="date_range" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.creationDate` })}
                        ：
                      </span>
                      <span className="product-lib-org-management-mirror-lib-list-card-footer-text">{creationDate}</span>
                    </Col>
                    <Col span={5} className="product-lib-org-management-mirror-lib-list-card-header-project">
                      <Icon type="dns-o" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.repoCount`, defaultMessage: '镜像数' })}
                        ：
                      </span>
                      <span className="product-lib-org-management-mirror-lib-list-card-footer-text">{repoCount}</span>
                    </Col>
                    <Col span={1} className="product-lib-org-management-mirror-lib-list-card-footer-action">
                      <div style={{ position: 'absolute', top: '-0.2rem', right: 0 }}>{renderAction(item)}</div>
                    </Col>
                  </Row>
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (
    <div className="product-lib-org-management-mirror-lib">
      <Spin dataSet={mirrorLibDs}>
        <div className="product-lib-org-management-mirror-lib-list">
          {renderFilterForm()}
          {
            listData && listData.length > 0 ? (
              <>
                <div className="product-lib-org-management-mirror-lib-list-body">
                  {renderData()}
                </div>
                <div className="product-lib-org-management-mirror-lib-pagination">
                  <Pagination dataSet={mirrorLibDs} />
                </div>
              </>
            ) : (
                // eslint-disable-next-line react/jsx-indent
                <div className="product-lib-org-management-mirror-lib-list-no-content">
                  {formatMessage({ id: `${intlPrefix}.view.noContent` })}
                </div>
            )
          }
        </div>
      </Spin>
    </div>
  );
};

export default observer(MirrorLib);
