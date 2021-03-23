import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { observer, useLocalStore } from 'mobx-react-lite';
import { DatePicker } from 'choerodon-ui';
import { Stores, Select, TextField } from 'choerodon-ui/pro';
import PureTimeLine from './PureTimeLine.js';
import { TabKeyEnum } from '../../MavenTabContainer';
import './log.less';

const { RangePicker } = DatePicker;
const { Option } = Select;

const OptLog = ({ mavenOptLogDs, activeTabKey, repositoryId, activeRepository }) => {
  const [isMore, setLoadMoreBtn] = useState(false);
  const [operateTypeLookupData, setOperateTypeLookupData] = useState([]);

  const {
    downloadTimes,
    personTimes,
  } = activeRepository;

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
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.AUTH_OPERATE_TYPE');
    setOperateTypeLookupData(lookupData);
  }

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    const res = await mavenOptLogDs.query(page);
    const records = timeLineStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        mavenOptLogDs.unshift(...records);
      }
      timeLineStore.setOldOptsRecord(mavenOptLogDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    } else {
      return false;
    }
  }, [mavenOptLogDs]);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OPTLOG) {
      mavenOptLogDs.setQueryParameter('repositoryId', repositoryId);
      mavenOptLogDs.setQueryParameter('repoType', 'MAVEN');
      loadData();
    }
  }, [activeTabKey]);

  useEffect(() => {
    getOperateTypeLookupData();
  }, []);

  const handleSearch = (params) => {
    Object.entries(params).forEach(o => { mavenOptLogDs.setQueryParameter(o[0], o[1]); });
    loadData();
  };

  const timeLineProps = useMemo(() => ({ isMore, operateTypeLookupData, loadData, mavenOptLogDs }), [isMore, operateTypeLookupData, loadData, mavenOptLogDs]);
  return (
    <div className="prod-lib-npm-optlog-timeline-container">
      <div className="prod-lib-npm-optlog-header">
        <div className="prod-lib-npm-optlog-header-count">
          <div className="prod-lib-npm-optlog-header-count-item">
            <span>拉取总次数：</span>
            <span>{`${downloadTimes || '0'}次`}</span>
          </div>
          <div className="prod-lib-npm-optlog-header-count-item">
            <span>拉取总人数：</span>
            <span>{`${personTimes || '0'}人`}</span>
          </div>
        </div>
        <div className="prod-lib-npm-optlog-search">
          <TextField onChange={(value) => handleSearch({ realName: value })} placeholder="用户名" />
          <RangePicker onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : '', endDate: dateString[1] ? `${dateString[1]} 23:59:59` : '' })} style={{ marginLeft: '0.12rem' }} />
          <Select onChange={(value) => handleSearch({ operateType: value })} style={{ marginLeft: '0.12rem' }} placeholder="操作类型">
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
