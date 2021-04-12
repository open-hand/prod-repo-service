/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Icon, Row, Col } from 'choerodon-ui';
import { Pagination, Spin, Modal, Form, TextField, Button, DataSet, Select } from 'choerodon-ui/pro';
import { Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
// import UserAvatar from '@/components/user-avatar';
import { isEmpty } from 'lodash';
import { useDockerStore } from '../stores';
import { useProdStore } from '../../../stores';
import TagListDS from '../stores/TagListDS';
import DescriptionModal from '../modals/mirror-description/index';
import TagList from './TagList';
import './index.less';

const modalKey = Modal.key();

const MirrorList = () => {
  const { prodStore: { getDockerRepoInfo, getSelectedMenu } } = useProdStore();
  const {
    organizationId,
    tabs: {
      LIST_TAB,
    },
    dockerStore,
    intlPrefix,
    intl: { formatMessage },
    mirrorListDS,
    repoListDs,
    scanDetailDs,
    getCurrentTheme,
  } = useDockerStore();
  const { getTabKey } = dockerStore;

  useEffect(() => {
    if (getTabKey === LIST_TAB) {
      if (!isEmpty(getDockerRepoInfo)) {
        mirrorListDS.queryDataSet.records[0].set('code', getDockerRepoInfo.code);
        mirrorListDS.queryDataSet.records[0].set('name', getDockerRepoInfo.name);
        mirrorListDS.query();
      }
      mirrorListDS.queryDataSet.validate();
    }
  }, [getTabKey, getSelectedMenu, getDockerRepoInfo]);
  const listData = mirrorListDS.current && mirrorListDS.toData();

  function openTagModal(imageName, repoName, projectId) {
    const tagListDs = new DataSet(TagListDS({ intlPrefix, formatMessage, repoName, organizationId, projectId }));
    const tagPros = {
      formatMessage,
      intlPrefix,
      organizationId,
      dockerStore,
      repoName,
      imageName,
      mirrorListDS,
      dataSet: tagListDs,
      repoListDs,
      scanDetailDs,
      getCurrentTheme,
    };

    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.tag.title` }, { name: repoName }),
      style: {
        width: '10.90rem',
      },
      className: 'product-lib-org-management-mirror-list-guide-modal',
      children: <TagList {...tagPros} />,
      okText: '扫描',
    });
  }

  function openDescription(description) {
    Modal.open({
      title: formatMessage({ id: `${intlPrefix}.model.description` }),
      children: <DescriptionModal description={description} />,
      key: modalKey,
      drawer: true,
      style: { width: '3.8rem' },
      destroyOnClose: true,
      okText: formatMessage({ id: 'close' }),
      okCancel: false,
    });
  }

  const handleSearch = () => {
    mirrorListDS.query();
  };

  const renderer = ({ text, record }) => (
    <div style={{ width: '100%' }}>
      {text} {text && `(${record.get('code')})`}
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
        dataSet={mirrorListDS.queryDataSet}
        labelLayout="float"
        columns={9}
        className="product-lib-org-management-mirror-list-filter-form"
      >
        <Select
          name="code"
          onChange={handleSearch}
          optionRenderer={optionRenderer}
          renderer={renderer}
          colSpan={2}
          searchable
          clearButton
        />
        <TextField name="imageName" onChange={handleSearch} colSpan={2} />
        <div colSpan={5} style={{ width: '0.46rem', float: 'right' }}>
          <Button funcType="raised" type="reset" className="product-lib-org-management-mirror-list-filter-form-btn" >
            {formatMessage({ id: 'reset' })}
          </Button>
        </div>
      </Form>
    );
  }

  const renderAction = (item) => {
    const actionData = [{
      service: [],
      text: formatMessage({ id: `${intlPrefix}.model.description` }),
      action: () => openDescription(item.description),
    },
      // {
      // service: [],
      // text: formatMessage({ id: 'delete' }),
      // action: () => openDelete(item),
      // }
    ];
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
            const { imageId, imageName, repoName, pullCount, updateTime, tagsCount, projectId } = item;
            return (
              <li key={imageId}>
                <div className="product-lib-org-management-mirror-list-list-card">
                  <Row className="product-lib-org-management-mirror-list-list-card-header" type="flex" justify="space-between">
                    <Col span={20} className="product-lib-org-management-mirror-list-list-card-header-icon">
                      <span
                        className="product-lib-org-management-mirror-list-list-card-header-title c7ncd-prolib-clickText"
                        onClick={() => openTagModal(imageName, repoName, projectId)}
                      >
                        {imageName}
                      </span>
                      {/* <Icon type="local_offer-o" onClick={() => openTagModal(imageName, repoName, projectId)} /> */}
                    </Col>
                    {/* <Col span={4} className="product-lib-org-management-mirror-list-list-card-header-project">
                      <div style={{ display: 'inline-flex' }}>
                        <UserAvatar
                          user={{
                            loginName: projectName,
                            realName: projectName,
                            imageUrl: projectImgUrl, // TODO
                          }}
                          size="0.18rem"
                          hiddenText
                          showToolTip={false}
                        />
                      </div>
                      <span className="product-lib-org-management-mirror-list-list-card-header-project-name">{projectName}</span>
                    </Col> */}
                  </Row>
                  <Row className="product-lib-org-management-mirror-list-list-card-header" type="flex" justify="space-between" >
                    <Col span={9} className="product-lib-org-management-mirror-list-list-card-footer">
                      <Icon type="date_range" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.updateTime` })}：</span>
                      <span className="product-lib-org-management-mirror-list-list-card-footer-text">{moment(updateTime).format('YYYY-MM-DD HH:mm:ss')}</span>
                    </Col>
                    <Col span={6} className="product-lib-org-management-mirror-list-list-card-footer">
                      <Icon type="book-o" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.tagsCount` })}：</span>
                      <span className="product-lib-org-management-mirror-list-list-card-footer-text">{tagsCount}</span>
                    </Col>
                    <Col span={6} className="product-lib-org-management-mirror-list-list-card-footer">
                      <Icon type="get_app-o" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.pullCount` })}：</span>
                      <span className="product-lib-org-management-mirror-list-list-card-footer-text">{pullCount}</span>
                    </Col>
                    <Col span={1} className="product-lib-org-management-mirror-list-list-card-footer-action">
                      <div style={{ position: 'absolute', top: '-0.05rem', right: 0 }}>{renderAction(item)}</div>
                    </Col>
                    {/* <Col span={2} className="product-lib-org-management-mirror-list-list-card-header-btn">
                      <div
                        onClick={() => handleOpenModal(name)}
                        className="product-lib-org-management-mirror-list-list-card-header-btn-delete"
                      >
                        {formatMessage({ id: 'delete' })}
                      </div>
                    </Col> */}
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
    <div className="product-lib-org-management-mirror-list">
      <Spin dataSet={mirrorListDS}>
        <div className="product-lib-org-management-mirror-list-list">
          {renderFilterForm()}
          {
            listData && listData.length > 0 ? (
              <React.Fragment>
                <div className="product-lib-org-management-mirror-list-list-body">
                  {renderData()}
                </div>
                <div className="product-lib-org-management-mirror-list-pagination">
                  <Pagination dataSet={mirrorListDS} />
                </div>
              </React.Fragment>
            ) : (
                // eslint-disable-next-line react/jsx-indent
                <div className="product-lib-org-management-mirror-list-list-no-content">
                  {formatMessage({ id: `${intlPrefix}.view.noContent` })}
                </div>
              )
          }
        </div>
      </Spin>
    </div>
  );
};

export default observer(MirrorList);
