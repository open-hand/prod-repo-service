/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState } from 'react';
import { Icon, Row, Col } from 'choerodon-ui';
import { Stores, Pagination, Spin, Modal, Form, Select, Button, TextField } from 'choerodon-ui/pro';
import { Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import GuideModal from './modals/GuideModal';
import { useNpmStore } from '../stores';
import { useProdStore } from '../../../stores';
import './index.less';

const LibList = () => {
  const { prodStore: { setNpmPackageId, getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      LIB_TAB,
      LIST_TAB,
    },
    intlPrefix,
    npmStore,
    intl: { formatMessage },
    libListDs,
    organizationId,
  } = useNpmStore();
  const { setTabKey, getTabKey } = npmStore;

  const [typeList, setTypeList] = useState([]);
  async function getTypeList() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.MAVEN_REPOSITORY_TYPE');
    setTypeList(lookupData);
  }

  function refresh() {
    libListDs.query();
  }
  useEffect(() => {
    if (getTabKey === LIB_TAB) {
      getTypeList();
      refresh();
    }
  }, [getTabKey, getSelectedMenu]);
  const listData = libListDs.current && libListDs.toData();

  const handleToPackage = (repositoryId) => {
    setTabKey(LIST_TAB);
    setNpmPackageId(repositoryId);
  };

  function renderFilterForm() {
    return (
      <Form
        dataSet={libListDs.queryDataSet}
        labelLayout="float"
        columns={9}
        className="product-lib-org-management-lib-list-filter-form"
      >
        <TextField name="repositoryName" onChange={refresh} colSpan={2} />
        <Select
          name="projectId"
          onChange={refresh}
          colSpan={2}
          searchable
          clearButton
        />
        <Select
          name="type"
          onChange={refresh}
          colSpan={2}
          searchable
          clearButton
        />
        {/* eslint-disable-next-line */}
        <div colSpan={3} style={{ width: '0.46rem', float: 'right' }}>
          <Button funcType="raised" type="reset" className="product-lib-org-management-lib-list-filter-form-btn">
            {formatMessage({ id: 'reset' })}
          </Button>
        </div>
      </Form>
    );
  }

  const handleOpenModal = (item) => {
    const key = Modal.key();
    Modal.open({
      key,
      title:
        formatMessage(
          {
            id: `${intlPrefix}.view.configGuide`,
            defaultMessage: `${item.name}配置指引`,
          },
          { name: item.name },
        ),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '7.4rem' },
      children: <GuideModal intlPrefix={intlPrefix} guideInfo={item} formatMessage={formatMessage} organizationId={organizationId} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  };

  const getTypeName = (code) => {
    const item = typeList.find(o => o.value === code);
    return item && item.meaning;
  };

  const renderAction = (item) => {
    const actionData = [{
      service: [],
      text: formatMessage({ id: `${intlPrefix}.view.guide` }),
      action: () => handleOpenModal(item),
    }, {
      service: [],
      text: formatMessage({ id: `${intlPrefix}.view.seeDetail` }),
      action: () => handleToPackage(item.repositoryId),
    }];
    return (
      <Action
        style={{ color: '#000' }}
        placement="bottomRight"
        data={actionData}
      />
    );
  };

  function renderData() {
    return listData ? (
      <ul>
        {
          listData.map(item => {
            const { repositoryId, neRepositoryName, projectName, projectImgUrl, creatorLoginName, creatorRealName, creationDate, type } = item;
            return (
              <li key={repositoryId + neRepositoryName}>
                <div className="product-lib-org-management-lib-list-list-card">
                  <Row className="product-lib-org-management-lib-list-list-card-header">
                    <Col span={9} className="product-lib-org-management-lib-list-list-card-header-icon">
                      {/* {rendererOnlineStatus(online)} */}
                      <span
                        className="product-lib-org-management-lib-list-list-card-header-title"
                        onClick={() => handleToPackage(item.repositoryId)}
                      >
                        {neRepositoryName}
                      </span>
                    </Col>
                    <Col span={12} className="product-lib-org-management-lib-list-list-card-header-project">
                      <div style={{ display: 'inline-flex' }}>
                        <UserAvatar
                          user={{
                            loginName: projectName,
                            realName: projectName,
                            imageUrl: projectImgUrl,
                          }}
                          size="0.18rem"
                          hiddenText
                          showToolTip={false}
                        />
                      </div>
                      <span className="product-lib-org-management-lib-list-list-card-header-project-name">{projectName}</span>
                    </Col>
                  </Row>
                  <Row className="product-lib-org-management-lib-list-list-card-footer">
                    <Col span={9}>
                      <Icon type="account_circle-o" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.createdBy` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{creatorRealName ? `${creatorRealName} (${creatorLoginName})` : ''}</span>
                    </Col>
                    <Col span={9} className="product-lib-org-management-lib-list-list-card-header-project">
                      <Icon type="date_range" />
                      <span >{formatMessage({ id: `${intlPrefix}.model.creationDate` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{creationDate}</span>
                    </Col>
                    <Col span={5} className="product-lib-org-management-lib-list-list-card-header-project">
                      <Icon type="category-o" />
                      <span >{formatMessage({ id: `${intlPrefix}.model.type` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{getTypeName(type)}</span>
                    </Col>
                    <Col span={1} className="product-lib-org-management-lib-list-list-card-footer-action">
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
    <div className="product-lib-org-management-lib-list">
      <Spin dataSet={libListDs}>
        <div className="product-lib-org-management-lib-list-list">
          {renderFilterForm()}
          {
            listData && listData.length > 0 ? (
              <React.Fragment>
                <div className="product-lib-org-management-lib-list-list-body">
                  {renderData()}
                </div>
                <div className="product-lib-org-management-lib-list-pagination">
                  <Pagination dataSet={libListDs} />
                </div>
              </React.Fragment>
            ) : (
                // eslint-disable-next-line react/jsx-indent
                <div className="product-lib-org-management-lib-list-list-no-content">
                  {formatMessage({ id: `${intlPrefix}.view.noContent` })}
                </div>
              )
          }
        </div>
      </Spin>
    </div>
  );
};

export default observer(LibList);
