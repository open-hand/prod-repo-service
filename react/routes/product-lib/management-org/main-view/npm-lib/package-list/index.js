/**
* 制品库自建或关联仓库查询
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Icon, Row, Col } from 'choerodon-ui';
import { Pagination, Spin, Modal, Form, TextField, Button, DataSet, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { isUndefined } from 'lodash';
import { useNpmStore } from '../stores';
import { useProdStore } from '../../../stores';
import TagListDS from '../stores/TagListDS';
import TagList from './TagList';
import './index.less';

const modalKey = Modal.key();

const MirrorList = () => {
  const { prodStore: { getNpmPackageName, getSelectedMenu } } = useProdStore();
  const {
    organizationId,
    tabs: {
      LIST_TAB,
    },
    npmStore,
    intlPrefix,
    intl: { formatMessage },
    packageListDs,
  } = useNpmStore();
  const { getTabKey } = npmStore;

  useEffect(() => {
    if (getTabKey === LIST_TAB) {
      if (!isUndefined(getNpmPackageName)) {
        packageListDs.queryDataSet.records[0].set('repositoryName', getNpmPackageName);
        packageListDs.query();
      }
      packageListDs.queryDataSet.validate();
    }
  }, [getTabKey, getSelectedMenu, getNpmPackageName]);
  const listData = packageListDs.current && packageListDs.toData();

  function openTagModal(name, repository) {
    const tagListDs = new DataSet(TagListDS({ intlPrefix, formatMessage, organizationId, repositoryName: repository, name }));
    const tagPros = {
      formatMessage,
      intlPrefix,
      organizationId,
      npmStore,
      imageName: name,
      packageListDs,
      dataSet: tagListDs,
    };

    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.tag.title` }, { name }),
      style: {
        width: '10.90rem',
      },
      className: 'product-lib-org-management-package-list-guide-modal',
      children: <TagList {...tagPros} />,
      footer: (okBtn) => okBtn,
      okText: formatMessage({ id: 'close' }),
    });
  }

  const handleSearch = () => {
    packageListDs.query();
  };

  function renderFilterForm() {
    return (
      <Form
        dataSet={packageListDs.queryDataSet}
        labelLayout="float"
        columns={9}
        className="product-lib-org-management-package-list-filter-form"
      >
        <Select name="repositoryName" onChange={handleSearch} colSpan={2} />
        <TextField name="name" onChange={handleSearch} colSpan={2} />
        <div colSpan={5} style={{ width: '0.46rem', float: 'right' }}>
          <Button funcType="raised" type="reset" className="product-lib-org-management-package-list-filter-form-btn">
            {formatMessage({ id: 'reset' })}
          </Button>
        </div>
      </Form>
    );
  }

  function renderData() {
    return listData ? (
      <ul>
        {
          listData.map(item => {
            const { id, name, repository, newestVersion, versionCount } = item;
            return (
              <li key={id}>
                <div className="product-lib-org-management-package-list-list-card">
                  <Row className="product-lib-org-management-package-list-list-card-header" type="flex" justify="space-between" style={{ marginBottom: '0.08rem' }}>
                    <Col span={20} className="product-lib-org-management-package-list-list-card-header-icon">
                      <span
                        className="product-lib-org-management-package-list-list-card-header-title"
                        onClick={() => openTagModal(name, repository)}
                      >
                        {name}
                      </span>
                    </Col>
                  </Row>
                  <Row className="product-lib-org-management-package-list-list-card-header" type="flex">
                    <Col span={10} className="product-lib-org-management-package-list-list-card-footer">
                      <Icon type="date_range" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.newestVersion` })}：</span>
                      <span className="product-lib-org-management-package-list-list-card-footer-text">{newestVersion}</span>
                    </Col>
                    <Col span={10} className="product-lib-org-management-package-list-list-card-footer">
                      <Icon type="book-o" />
                      <span>{formatMessage({ id: `${intlPrefix}.model.tagsCount` })}：</span>
                      <span className="product-lib-org-management-package-list-list-card-footer-text">{versionCount}</span>
                    </Col>
                    {/* <Col span={1} className="product-lib-org-management-package-list-list-card-footer-action">
                      <div style={{ position: 'absolute', top: '-0.15rem', right: 0 }}>{renderAction(item)}</div>
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
    <div className="product-lib-org-management-package-list">
      <Spin dataSet={packageListDs}>
        <div className="product-lib-org-management-package-list-list">
          {renderFilterForm()}
          {
            listData && listData.length > 0 ? (
              <React.Fragment>
                <div className="product-lib-org-management-package-list-list-body">
                  {renderData()}
                </div>
                <div className="product-lib-org-management-package-list-pagination">
                  <Pagination dataSet={packageListDs} />
                </div>
              </React.Fragment>
            ) : (
                // eslint-disable-next-line react/jsx-indent
                <div className="product-lib-org-management-package-list-list-no-content">
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
