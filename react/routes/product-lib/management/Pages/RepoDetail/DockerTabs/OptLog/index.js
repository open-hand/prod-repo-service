import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { observer, useLocalStore } from 'mobx-react-lite';
import { DatePicker, Radio } from 'choerodon-ui';
import { Stores, Select, TextField } from 'choerodon-ui/pro';
import PureTimeLine from './PureTimeLine.js';
import { TabKeyEnum } from '../../DockerTabContainer';
import './index.less';

const { RangePicker } = DatePicker;
const { Option } = Select;

const OptLog = ({ formatMessage, optLogDs, activeTabKey }) => {
  const [isMore, setLoadMoreBtn] = useState(false);
  const [operateTypeLookupData, setOperateTypeLookupData] = useState([]);
  const [logTabKey, setLogTabKey] = useState('AuthLog');

  const handleTabChange = useCallback((e) => {
    // optLogDs.queryDataSet.reset(); 不生效，只好手动清除
    optLogDs.setQueryParameter('imageName', undefined);
    optLogDs.setQueryParameter('startDate', undefined);
    optLogDs.setQueryParameter('endDate', undefined);
    optLogDs.setQueryParameter('operateType', undefined);
    setLogTabKey(e.target.value);
  }, [optLogDs]);

  const timeLineStore = useLocalStore(() => ({
    oldOptsRecord: [],
    setOldOptsRecord(data) {
      this.oldOptsRecord = data || [];
    },
    get getOldOptsRecord() {
      return this.oldOptsRecord;
    },
  }));

  async function getOperateTypeLookupData() {
    let lookupData = [];
    if (logTabKey === 'AuthLog') {
      lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.AUTH_OPERATE_TYPE');
    } else if (logTabKey === 'ImgLog') {
      lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.IMAGE_OPERATE_TYPE');
    }
    setOperateTypeLookupData(lookupData);
  }

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    optLogDs.queryDataSet.reset();
    const res = await optLogDs.query(page);
    const records = timeLineStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        optLogDs.unshift(...records);
      }
      timeLineStore.setOldOptsRecord(optLogDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    } else {
      return false;
    }
  }, [optLogDs]);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OPT_LOG) {
      optLogDs.logTabKey = logTabKey;
      loadData();
    }
  }, [activeTabKey, loadData, logTabKey]);

  useEffect(() => {
    getOperateTypeLookupData();
  }, [logTabKey]);

  const handleSearch = (params) => {
    Object.entries(params).forEach(o => { optLogDs.setQueryParameter(o[0], o[1]); });
    loadData();
  };

  const timeLineProps = useMemo(() => ({ isMore, operateTypeLookupData, loadData, optLogDs }), [isMore, operateTypeLookupData, loadData, optLogDs]);
  return (
    <div className="product-lib-timeline-container">
      <div className="product-lib-timeline-top">
        <Radio.Group
          value={logTabKey}
          onChange={handleTabChange}
          className="product-lib-tab-button-group"
        >
          <Radio.Button value="AuthLog">
            {formatMessage({ id: 'infra.prod.lib.view.authLog', defaultMessage: '权限操作记录' })}
          </Radio.Button>
          <Radio.Button value="ImgLog">
            {formatMessage({ id: 'infra.prod.lib.view.imgLog', defaultMessage: '镜像操作记录' })}
          </Radio.Button>
        </Radio.Group>
        <div key={logTabKey} className="product-lib-search">
          {logTabKey === 'AuthLog' &&
            <TextField
              onChange={(value) => handleSearch({ loginName: value })}
              placeholder={formatMessage({ id: 'infra.prod.lib.model.userName', defaultMessage: '用户名' })}
            />
          }
          {logTabKey === 'ImgLog' &&
            <TextField
              onChange={(value) => handleSearch({ imageName: value })}
              placeholder={formatMessage({ id: 'infra.prod.lib.model.imageName', defaultMessage: '镜像名' })}
            />
          }
          <RangePicker
            style={{ marginLeft: '0.12rem' }}
            onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : '', endDate: dateString[1] ? `${dateString[1]} 23:59:59` : '' })}
          />
          <Select
            onChange={(value) => handleSearch({ operateType: value })}
            style={{ marginLeft: '0.12rem' }}
            placeholder={formatMessage({ id: 'infra.doclib.audit.model.operateType', defaultMessage: '操作类型' })}
          >
            {
              operateTypeLookupData.map(o => (
                <Option key={o.value} value={o.value}>{o.meaning}</Option>
              ))
            }
          </Select>
        </div>
      </div>
      <PureTimeLine {...timeLineProps} />
    </div>
  );
};

export default observer(OptLog);
