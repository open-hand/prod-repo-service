/**
* 自定义docker仓库总览
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/6/9
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState, forwardRef, useImperativeHandle, useCallback } from 'react';
import { axios, stores } from '@choerodon/boot';
import { Spin } from 'choerodon-ui/pro';
import { useObserver } from 'mobx-react-lite';
import { TabKeyEnum } from '../../CustomDockerTabContainer';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const OverView = ({ repositoryId, formatMessage, activeTabKey }, ref) => {
  const [loading, setLoading] = useState(false);
  const [detail, setDetail] = useState({});

  const init = useCallback(async (repoId) => {
    const { currentMenuType: { organizationId } } = stores.AppState;
    try {
      setLoading(true);
      const res = await axios.get(`/rdupm/v1/${organizationId}/harbor-custom-repos/detail/project/${repoId}`);
      setDetail(res);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      init(repositoryId);
    }
  }, [activeTabKey, init]);

  useImperativeHandle(ref, () => ({
    init,
  }));

  return useObserver(() => (
    <Spin spinning={loading}>
      <div className="product-lib-custom-docker-overview">
        <div className="product-lib-custom-docker-overview-field">
          <label className="product-lib-custom-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.view.createDockerType`, defaultMessage: '仓库来源' })}
          </label>
          <span className="product-lib-custom-docker-overview-field-value">
            自定义类型
          </span>
        </div>

        <div className="product-lib-custom-docker-overview-field">
          <label className="product-lib-custom-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.repoName`, defaultMessage: '仓库名称' })}
          </label>
          <span className="product-lib-custom-docker-overview-field-value">
            {detail.repoName}
          </span>
        </div>

        <div className="product-lib-custom-docker-overview-field">
          <label className="product-lib-custom-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.repoUrl`, defaultMessage: '仓库地址' })}
          </label>
          <span className="product-lib-custom-docker-overview-field-value">
            {detail.repoUrl}
          </span>
        </div>

        <div className="product-lib-custom-docker-overview-field">
          <label className="product-lib-custom-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.projectShare`, defaultMessage: '是否共享' })}
          </label>
          <span className="product-lib-custom-docker-overview-field-value">
            {detail.projectShare === 'true' ? '是' : '否'}
          </span>
        </div>

        <div className="product-lib-custom-docker-overview-field">
          <label className="product-lib-custom-docker-overview-field-label">
            {formatMessage({ id: 'description', defaultMessage: '描述' })}
          </label>
          <span className="product-lib-custom-docker-overview-field-value">
            {detail.description}
          </span>
        </div>

      </div>
    </Spin>
  ));
};

export default forwardRef(OverView);
