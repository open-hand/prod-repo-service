/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState } from 'react';
import { Icon, Row, Col } from 'choerodon-ui';
import {
  Stores, Pagination, Spin, Form, Select, Button, TextField,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import { useNpmStore } from '../stores';
import { useProdStore } from '../../../stores';
import AssignRepo from '../../assign-repo';
import './index.less';

const LibList = () => {
  const { prodStore: { getSelectedMenu } } = useProdStore();
  const {
    intlPrefix,
    intl: { formatMessage },
    libListDs,
    formatCommon,
    format,
  } = useNpmStore();

  const [typeList, setTypeList] = useState([]);
  async function getTypeList() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.MAVEN_REPOSITORY_TYPE');
    setTypeList(lookupData);
  }

  const handleSearch = () => {
    libListDs.query();
  };
  useEffect(() => {
    getTypeList();
    handleSearch();
  }, [getSelectedMenu]);

  const listData = libListDs.current && libListDs.toData();

  function renderFilterForm() {
    return (
      <div style={{ display: 'flex' }}>
        <div>
          <Form
            dataSet={libListDs.queryDataSet}
            className="product-lib-site-management-lib-list-filter-form"
            columns={4}
          >
            <TextField name="repositoryName" onChange={handleSearch} colSpan={1} />
            <Select name="type" onChange={handleSearch} colSpan={1} />
            <Select name="versionPolicy" onChange={handleSearch} colSpan={1} />
            <Select
              colSpan={1}
              name="distributedQueryFlag"
              onChange={handleSearch}
            >
              <Select.Option value={1}>已分配</Select.Option>
              <Select.Option value={0}>未分配</Select.Option>
            </Select>
          </Form>
        </div>
        <div style={{ width: '0.46rem', flexShrink: 0, margin: '18px 0 0 65px' }}>
          <Button
            funcType="raised"
            type="reset"
            style={{ color: '#3F51B5' }}
            onClick={() => {
              libListDs.queryDataSet.current.reset();
              libListDs.query();
            }}
          >
            {formatCommon({ id: 'reset' })}
          </Button>
        </div>
      </div>
    );
  }

  const getTypeName = (code) => {
    const item = typeList.find((o) => o.value === code);
    return item && item.meaning;
  };

  function renderData() {
    return listData ? (
      <ul>
        {
          listData.map((item) => {
            const {
              id,
              name,
              projectImgUrl,
              creatorLoginName, creatorRealName, creationDate, type, projectName, repositoryId,
            } = item;
            return (
              <li key={id + name}>
                <div className="product-lib-site-management-lib-list-list-card">
                  <Row className="product-lib-site-management-lib-list-list-card-header">
                    <Col span={9} className="product-lib-site-management-lib-list-list-card-header-icon">
                      {/* {rendererOnlineStatus(online)} */}
                      <AssignRepo formatMessage={formatMessage} name={name} libListDs={libListDs} repositoryId={repositoryId} item={item} repoType="NPM" />
                    </Col>
                    <Col span={12} className="product-lib-site-management-lib-list-list-card-header-project">
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
                      <span className="product-lib-site-management-lib-list-list-card-header-project-name">{projectName}</span>
                    </Col>
                  </Row>
                  <Row className="product-lib-site-management-lib-list-list-card-footer">
                    <Col span={9}>
                      <Icon type="account_circle-o" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.createdBy` })}
                        ：
                      </span>
                      <span className="product-lib-site-management-lib-list-list-card-footer-text">{creatorRealName ? `${creatorRealName} (${creatorLoginName})` : ''}</span>
                    </Col>
                    <Col span={9} className="product-lib-site-management-lib-list-list-card-header-project">
                      <Icon type="date_range" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.creationDate` })}
                        ：
                      </span>
                      <span className="product-lib-site-management-lib-list-list-card-footer-text">{creationDate}</span>
                    </Col>
                    <Col span={5} className="product-lib-site-management-lib-list-list-card-header-project">
                      <Icon type="category-o" />
                      <span>
                        {formatMessage({ id: `${intlPrefix}.model.type` })}
                        ：
                      </span>
                      <span className="product-lib-site-management-lib-list-list-card-footer-text">{getTypeName(type)}</span>
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
    <div className="product-lib-site-management-lib-list">
      <Spin dataSet={libListDs}>
        <div className="product-lib-site-management-lib-list-list">
          {renderFilterForm()}
          {
            listData && listData.length > 0 ? (
              <>
                <div className="product-lib-site-management-lib-list-list-body">
                  {renderData()}
                </div>
                <div className="product-lib-site-management-lib-list-pagination">
                  <Pagination dataSet={libListDs} />
                </div>
              </>
            ) : (
                // eslint-disable-next-line react/jsx-indent
                <div className="product-lib-site-management-lib-list-list-no-content">
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
