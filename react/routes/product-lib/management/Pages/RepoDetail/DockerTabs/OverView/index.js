/**
* 制品库自建或关联仓库查询
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState, forwardRef, useImperativeHandle, useCallback } from 'react';
import { axios } from '@choerodon/boot';
import { Spin } from 'choerodon-ui/pro';
import { useObserver } from 'mobx-react-lite';
import { TabKeyEnum } from '../../MavenTabContainer';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const OverView = ({ harborId, formatMessage, activeTabKey }, ref) => {
  const [loading, setLoading] = useState(false);
  const [detail, setDetail] = useState({});

  const init = useCallback(async (id) => {
    try {
      setLoading(true);
      const res = await axios.get(`/rdupm/v1/harbor-project/detail/${id}`);
      setDetail(res);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OVERVIEW) {
      init(harborId);
    }
  }, [activeTabKey, init]);

  useImperativeHandle(ref, () => ({
    init,
  }));

  return useObserver(() => (
    <Spin spinning={loading}>
      <div className="product-lib-docker-overview">
        <div className="product-lib-docker-overview-field">
          <label className="product-lib-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.publicFlag`, defaultMessage: '访问级别' })}
          </label>
          <span className="product-lib-docker-overview-field-value">
            {detail.publicFlag === 'true' ? '公开' : '不公开'}
          </span>
        </div>

        <div className="product-lib-docker-overview-field">
          <label className="product-lib-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.repoCount`, defaultMessage: '镜像数' })}
          </label>
          <span className="product-lib-docker-overview-field-value">
            {detail.repoCount}
          </span>
        </div>

        <div className="product-lib-docker-overview-field">
          <label className="product-lib-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.view.projectCapacity`, defaultMessage: '项目容量' })}
          </label>
          <span className="product-lib-docker-overview-field-value">
            <div className="product-lib-docker-overview-field-progress">
              <div className="product-lib-docker-overview-field-progress-top">
                <div>
                  {formatMessage({ id: `${intlPrefix}.model.countLimit`, defaultMessage: '制品数' })}
                </div>
                <div>
                  {`(${detail.usedCount}/${detail.countLimit})`}
                </div>
              </div>
              <div
                className="product-lib-docker-overview-field-progress-bar"
                style={{
                  background: 'rgba(0,0,0,0.04)',
                  width: '240px',
                }}
              >
                <div
                  className="product-lib-docker-overview-field-progress-bar"
                  style={{
                    background: 'rgba(0,191,165,1)',
                    width: `${(detail.usedCount / detail.countLimit) * 240}px`,
                  }}
                />
              </div>
            </div>
            <div className="product-lib-docker-overview-field-progress">
              <div className="product-lib-docker-overview-field-progress-top">
                <div>
                  {formatMessage({ id: `${intlPrefix}.view.usedStorage`, defaultMessage: '存储消耗' })}
                </div>
                <div>
                  {`(${detail.usedStorageNum}${detail.usedStorageUnit}/${detail.storageNum}${detail.storageUnit})`}
                </div>
              </div>
              <div
                className="product-lib-docker-overview-field-progress-bar"
                style={{
                  background: 'rgba(0,0,0,0.04)',
                  width: '240px',
                }}
              >
                <div
                  className="product-lib-docker-overview-field-progress-bar"
                  style={{
                    background: 'rgba(255, 177, 0, 1)',
                    width: `${(detail.usedStorage / detail.storageLimit) * 240}px`,
                  }}
                />
              </div>
            </div>
          </span>
        </div>

        <div className="product-lib-docker-overview-field">
          <label className="product-lib-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.view.deploySafe`, defaultMessage: '部署安全' })}
          </label>
          <span className="product-lib-docker-overview-field-value">
            {detail.autoScanFlag === 'true' &&
              (
                <div>
                  {formatMessage({ id: 'infra.prod.lib.model.autoScanFlag', defaultMessage: '自动扫描镜像' })}
                </div>
              )
            }
            {detail.preventVulnerableFlag === 'true' &&
              (
                <div>
                  {formatMessage({ id: 'infra.prod.lib.model.preventVulnerableFlag', defaultMessage: '阻止潜在漏洞镜像' })}
                </div>
              )
            }
          </span>
        </div>

        <div className="product-lib-docker-overview-field">
          <label className="product-lib-docker-overview-field-label">
            {formatMessage({ id: `${intlPrefix}.model.cve`, defaultMessage: 'CVE白名单' })}
          </label>
          <span className="product-lib-docker-overview-field-value">
            {(detail.cveNoList || []).join(',')}
          </span>
        </div>


      </div>
    </Spin>
  ));
};

export default forwardRef(OverView);
