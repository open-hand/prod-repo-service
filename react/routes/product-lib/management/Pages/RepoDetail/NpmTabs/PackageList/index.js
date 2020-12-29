/**
* 镜像列表
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/28
* @copyright 2020 ® HAND
*/
import React, { useEffect, useMemo } from 'react';
import { Icon, message } from 'choerodon-ui';
import { Spin, Form, TextField, Pagination, Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { axios, stores, Action } from '@choerodon/boot';
import { TabKeyEnum } from '../../NpmTabContainer';
import TagModal from './TagModal';
import { useUserAuth } from '../../../index';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const PackageList = ({ npmOverViewDs, npmComponentDs, formatMessage, activeTabKey, repositoryId, repositoryName, enableFlag }) => {
  const userAuth = useUserAuth();
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.PACKAGE_LIST) {
      npmComponentDs.setQueryParameter('repositoryName', repositoryName);
      npmComponentDs.setQueryParameter('repositoryId', repositoryId);
      npmComponentDs.query();
    }
  }, [activeTabKey, repositoryName, repositoryId]);

  const handleDelete = async (data) => {
    const { repository, componentIds, name } = data;
    const deleteKey = Modal.key();
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    Modal.open({
      key: deleteKey,
      title: formatMessage({ id: 'confirm.delete' }),
      children: formatMessage({ id: 'infra.prod.lib.view.deleteNpm', defaultMessage: `确认删除包${name}?若删除包，则该包下所有版本将被删除` }, { name }),
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
      onOk: async () => {
        try {
          await axios.delete(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/npm?repositoryId=${repositoryId}&&repositoryName=${repository}`, { data: componentIds });
          message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
          npmComponentDs.query();
        } catch (error) {
          // message.error(error);
        }
      },
      footer: ((okBtn, cancelBtn) => (
        <React.Fragment>
          {cancelBtn}{okBtn}
        </React.Fragment>
      )),
      movable: false,
    });
  };

  const tagModalProps = useMemo(() => ({ formatMessage, npmOverViewDs, repositoryId, repositoryName, userAuth, enableFlag }), [repositoryId, enableFlag, userAuth, repositoryName, formatMessage, npmOverViewDs]);

  const openTagModal = (data) => {
    const { name } = data;
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: 'infra.prod.lib.view.npmtag.title', defaultMessage: `${name}版本` }, { name }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-edit-model',
      style: { width: '75%' },
      children: <TagModal {...tagModalProps} name={name} />,
    });
  };


  const packageList = useMemo(() => npmComponentDs.toData(), [npmComponentDs.data]);

  return (
    <Spin dataSet={npmComponentDs}>
      <div
        className="product-lib-npm-imagelist-search"
        onKeyDown={(event) => {
          if (event.keyCode === 13) {
            npmComponentDs.query();
          }
        }}
      >
        <Form dataSet={npmComponentDs.queryDataSet} >
          <TextField name="name" />
        </Form>
      </div>
      {npmComponentDs.records.length > 0 ?
        <React.Fragment>
          <ul className="product-lib-npm-imagelist-list">
            {
              packageList.map(data => {
                const { id, name, lastUpdateDate, versionCount, newestVersion } = data;
                return (
                  <li
                    key={id}
                    className="product-lib-npm-imagelist-record-card"
                  >
                    <div style={{ width: '100%', paddingBottom: '10px' }}>
                      <div className="product-lib-npm-imagelist-record-card-image-name" onClick={() => openTagModal(data)}>
                        <span className="link-cell">{name}</span>
                      </div>
                      <div style={{ display: 'flex', marginTop: '10px' }}>
                        <div className="product-lib-npm-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="date_range-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.updateTime`, defaultMessage: '最新更新时间' })}：`}
                          <span style={{ color: 'rgba(0,0,0,1)' }}>{moment(lastUpdateDate).format('YYYY-MM-DD HH:mm:ss')}</span>
                        </div>

                        <div className="product-lib-npm-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="beenhere-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.newestVersion`, defaultMessage: '最新版本' })}：`}
                          <span style={{ color: 'rgba(0,0,0,1)' }}>{newestVersion}</span>
                        </div>

                        <div className="product-lib-npm-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="book-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.tagsCount`, defaultMessage: '版本数' })}：`}
                          <span style={{ color: 'rgba(0,0,0,1)' }}>{versionCount}</span>
                        </div>
                      </div>
                    </div>
                    <div>
                      {(userAuth?.includes('projectAdmin') || userAuth?.includes('developer')) && enableFlag === 'Y' &&
                        <Action
                          data={[
                            {
                              service: ['choerodon.code.project.infra.product-lib.ps.project-owner-npm'],
                              text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
                              action: () => handleDelete(data),
                            },
                          ]}
                        />
                      }
                    </div>
                  </li>
                );
              })
            }
          </ul>
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Pagination dataSet={npmComponentDs} />
          </div>
        </React.Fragment>
        :
        <div className="product-lib-npm-imagelist-no-content">
          <span>暂无数据</span>
        </div>
      }
    </Spin >
  );
};

export default observer(PackageList);
