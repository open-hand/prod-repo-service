/* eslint-disable max-len */
/**
* 镜像列表
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/28
* @copyright 2020 ® HAND
*/
import React, { useEffect, useMemo } from 'react';
import { Icon, message } from 'choerodon-ui';
import {
  Spin, Form, TextField, Pagination, Modal,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { axios, stores, Action } from '@choerodon/boot';
import { TabKeyEnum } from '../../DockerTabContainer';
import DescriptionModal from './DescriptionModal';
import TagModal from './TagModal';
import { useUserAuth } from '../../../index';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const ImageList = ({
  dockerImageTagDs, dockerImageListDs, dockerImageScanDetailsDs, formatMessage, activeTabKey,
}) => {
  const userAuth = useUserAuth();
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.DOCKER_IMAGE) {
      const { currentMenuType: { projectId } } = stores.AppState;
      dockerImageListDs.projectId = projectId;
      dockerImageListDs.query();
    }
  }, [activeTabKey]);

  const handleDelete = async (data) => {
    const { imageName } = data;
    const deleteKey = Modal.key();
    Modal.open({
      key: deleteKey,
      title: formatMessage({ id: 'confirm.delete' }),
      children: formatMessage({ id: 'infra.prod.lib.view.deleteImage', defaultMessage: `确认删除镜像${imageName}?若删除镜像，则该镜像下所有版本将被删除` }, { imageName }),
      okText: formatMessage({ id: 'delete' }),
      onOk: async () => {
        try {
          await axios.delete('/rdupm/v1/harbor-image/delete', { data });
          message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
          dockerImageListDs.query();
        } catch (error) {
          // message.error(error);
        }
      },
      footer: ((okBtn, cancelBtn) => (
        <>
          {cancelBtn}
          {okBtn}
        </>
      )),
      movable: false,
    });
  };

  const handleEdit = (data) => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: 'infra.prod.lib.view.imageDescription', defaultMessage: '镜像描述' }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-edit-model',
      children: <DescriptionModal dockerImageListDs={dockerImageListDs} originData={data} />,
    });
  };

  const openTagModal = (data) => {
    const { imageName: name, repoName } = data;
    const key = Modal.key();
    const { currentMenuType: { projectId } } = stores.AppState;
    Modal.open({
      key,
      title: formatMessage({ id: 'infra.prod.lib.view.tag.title', defaultMessage: `${name}镜像Tag` }, { name }),
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-edit-model',
      style: { width: '75%' },
      okProps: {
        disabled: true,
      },
      okText: '扫描',
      cancelText: '关闭',
      children: <TagModal projectId={projectId} dockerImageScanDetailsDs={dockerImageScanDetailsDs} dockerImageTagDs={dockerImageTagDs} formatMessage={formatMessage} repoName={repoName} imageName={name} userAuth={userAuth} />,
    });
  };

  const imageList = useMemo(() => dockerImageListDs.toData(), [dockerImageListDs.data]);

  return (
    <Spin dataSet={dockerImageListDs}>
      <div
        className="product-lib-docker-imagelist-search"
        onChange={() => {
          dockerImageListDs.query();
        }}
        role="none"
      >
        <Form dataSet={dockerImageListDs.queryDataSet}>
          <TextField name="imageName" />
        </Form>
      </div>
      {dockerImageListDs.records.length > 0
        ? (
          <>
            <ul className="product-lib-docker-imagelist-list">
              {
              imageList.map((data) => {
                const {
                  imageId, imageName, updateTime, tagsCount, pullCount,
                } = data;
                return (
                  <li
                    key={imageId}
                    className="product-lib-docker-imagelist-record-card"
                  >
                    <div style={{ width: '100%', paddingBottom: '10px' }}>
                      <div role="none" className="product-lib-docker-imagelist-record-card-image-name" onClick={() => openTagModal(data)}>
                        <span className="link-cell">{imageName}</span>
                      </div>
                      <div style={{ display: 'flex', marginTop: '10px' }}>
                        <div className="product-lib-docker-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="date_range-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.updateTime`, defaultMessage: '最新更新时间' })}：`}
                          <span style={{ color: 'var(--text-color)' }}>{moment(updateTime).format('YYYY-MM-DD HH:mm:ss')}</span>
                        </div>
                        <div className="product-lib-docker-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="book-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.tagsCount`, defaultMessage: '版本数' })}：`}
                          <span style={{ color: 'var(--text-color)' }}>{tagsCount}</span>
                        </div>

                        <div className="product-lib-docker-imagelist-record-card-updateTime" style={{ width: '30%' }}>
                          <Icon type="get_app-o" style={{ marginRight: '2px' }} />
                          {`${formatMessage({ id: `${intlPrefix}.model.pullCount`, defaultMessage: '下载数' })}：`}
                          <span style={{ color: 'var(--text-color)' }}>{pullCount}</span>
                        </div>
                      </div>
                    </div>
                    <div>
                      {(() => {
                        let actionData = [];
                        if (userAuth.includes('projectAdmin')) {
                          actionData = [
                            {
                              service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
                              text: formatMessage({ id: 'infra.prod.lib.view.imageDescription', defaultMessage: '镜像描述' }),
                              action: () => handleEdit(data),
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
                              service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
                              text: formatMessage({ id: 'infra.prod.lib.view.imageDescription', defaultMessage: '镜像描述' }),
                              action: () => handleEdit(data),
                            },
                          ];
                        }
                        return <Action data={actionData} />;
                      })()}
                    </div>
                  </li>
                );
              })
            }
            </ul>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Pagination dataSet={dockerImageListDs} />
            </div>
          </>
        )
        : (
          <div className="product-lib-docker-imagelist-no-content">
            <span>暂无数据</span>
          </div>
        )}
    </Spin>
  );
};

export default observer(ImageList);
