/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState } from 'react';
import { Icon, Row, Col } from 'choerodon-ui';
import { Pagination, Spin, Modal, Form, TextField, Select, Stores, Button } from 'choerodon-ui/pro';
import { axios, Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import { useMavenStore } from '../stores';
import { useProdStore } from '../../../stores';
import GuideModal from './modals/GuideModal';
import './index.less';


const MirrorLib = () => {
  const { prodStore: { setRepositoryId, getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      LIB_TAB,
      PACKAGE_TAB,
    },
    mavenStore,
    intlPrefix,
    intl: { formatMessage },
    libListDs,
  } = useMavenStore();
  const { setTabKey, getTabKey, setGuideInfo } = mavenStore;


  const [typeList, setTypeList] = useState([]);

  async function getTypeList() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.MAVEN_REPOSITORY_TYPE');
    setTypeList(lookupData);
  }

  useEffect(() => {
    if (getTabKey === LIB_TAB) {
      getTypeList();
      libListDs.query();
    }
  }, [getTabKey, getSelectedMenu]);
  const listData = libListDs.current && libListDs.toData();

  async function fetchGuide(name, repositoryId) {
    try {
      const res = await axios.get(`/rdupm/v1/nexus-repositorys/maven/repo/guide/${name}?repositoryId=${repositoryId}&showPushFlag=false`);
      setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }

  const handleOpenModal = (name, repositoryId) => {
    fetchGuide(name, repositoryId);
    const key = Modal.key();
    Modal.open({
      key,
      title:
        formatMessage(
          {
            id: `${intlPrefix}.view.configGuide`,
            defaultMessage: `${name}配置指引`,
          },
          { name },
        ),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '7.4rem' },
      children: <GuideModal guideInfo={mavenStore} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  };

  // const rendererOnlineStatus = (online) => (
  //   <Tooltip title={online ? formatMessage({ id: 'online', defaultMessage: '在线' }) : formatMessage({ id: 'offline', defaultMessage: '离线' })}>
  //     <span className={online ? 'product-lib-org-management-lib-list-status product-lib-org-management-lib-list-status-online' : 'product-lib-org-management-lib-list-status product-lib-org-management-lib-list-status-offline'} />
  //   </Tooltip>
  // );
  const handleToPackage = (repositoryId) => {
    setTabKey(PACKAGE_TAB);
    setRepositoryId(repositoryId);
  };

  const handleSearch = () => {
    libListDs.query();
  };

  function renderFilterForm() {
    return (
      <Form
        dataSet={libListDs.queryDataSet}
        labelLayout="float"
        columns={9}
        className="product-lib-org-management-lib-list-filter-form"
      >
        <TextField name="repositoryName" onChange={handleSearch} colSpan={2} />
        <Select
          name="type"
          onChange={handleSearch}
          colSpan={2}
          searchable
          clearButton
        />
        <Select
          name="versionPolicy"
          onChange={handleSearch}
          colSpan={2}
          searchable
          clearButton
        />
        <div colSpan={3} style={{ width: '0.46rem', float: 'right' }}>
          <Button funcType="raised" type="reset" className="product-lib-org-management-lib-list-filter-form-btn">
            {formatMessage({ id: 'reset' })}
          </Button>
        </div>
      </Form>
    );
  }

  const renderAction = (item) => {
    const actionData = [{
      service: [],
      text: formatMessage({ id: `${intlPrefix}.view.guide` }),
      action: () => handleOpenModal(item.name, item.repositoryId),
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


  const getTypeName = (code) => {
    const item = typeList.find(o => o.value === code);
    return item && item.meaning;
  };
  function renderData() {
    return listData ? (
      <ul>
        {
          listData.map(item => {
            const { repositoryId, projectName, creatorLoginName, creatorRealName, creationDate, name, projectImgUrl, type, versionPolicy } = item;
            return (
              <li key={repositoryId + name}>
                <div className="product-lib-org-management-lib-list-list-card">
                  <Row className="product-lib-org-management-lib-list-list-card-header">
                    <Col span={7} className="product-lib-org-management-lib-list-list-card-header-icon">
                      {/* {rendererOnlineStatus(online)} */}
                      <span
                        className="product-lib-org-management-lib-list-list-card-header-title"
                        onClick={() => handleToPackage(repositoryId)}
                      >
                        {name}
                      </span>
                    </Col>
                    <Col span={12} className="product-lib-org-management-lib-list-list-card-header-project">
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
                      <span className="product-lib-org-management-lib-list-list-card-header-project-name">{projectName}</span>
                    </Col>
                  </Row>
                  <Row className="product-lib-org-management-lib-list-list-card-footer">
                    <Col span={7}>
                      <Icon type="account_circle-o" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.createdBy` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{creatorRealName ? `${creatorRealName} (${creatorLoginName})` : ''}</span>
                    </Col>
                    <Col span={6} className="product-lib-org-management-lib-list-list-card-header-project">
                      <Icon type="date_range" />
                      <span >{formatMessage({ id: `${intlPrefix}.model.creationDate` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{creationDate}</span>
                    </Col>
                    <Col span={5} className="product-lib-org-management-lib-list-list-card-header-project">
                      <Icon type="category-o" />
                      <span >{formatMessage({ id: `${intlPrefix}.model.type` })}：</span>
                      <span className="product-lib-org-management-lib-list-list-card-footer-text">{getTypeName(type)}</span>
                    </Col>
                    {type !== 'group' ? (
                      <Col span={5} className="product-lib-org-management-lib-list-list-card-header-project">
                        <Icon type="list" />
                        <span >{formatMessage({ id: `${intlPrefix}.model.versionPolicy` })}：</span>
                        <span className="product-lib-org-management-lib-list-list-card-footer-text">{versionPolicy}</span>
                      </Col>
                    ) : (<Col span={5} />)}
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

export default observer(MirrorLib);
