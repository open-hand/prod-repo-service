/**
* 操作日志
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/05/6
* @copyright 2020 ® HAND
*/
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Stores, Select, TextField } from 'choerodon-ui/pro';
import { DatePicker } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { isEmpty } from 'lodash';
import { Content } from '@choerodon/boot';
import Empty from '@/components/empty';
import empty from '@/assets/empty.png';
import './log.less';
import { useMavenStore } from '../stores';
import { useProdStore } from '../../../stores';
import TimeLine from './TimeLine';

const { RangePicker } = DatePicker;
const { Option } = Select;

const ListView = observer(() => {
  const { prodStore: { getSelectedMenu } } = useProdStore();
  const {
    formatMessage,
    tabs: {
      LOG_TAB,
    },
    mavenStore,
    logListDs,
  } = useMavenStore();
  const { getTabKey, setOpeLoading, getLoading, getLoadMoreBtn } = mavenStore;

  const [opEventTypeLookupData, setOpEventTypeLookupData] = useState([]);
  const [searchParamFlag, setFlag] = useState(isEmpty(logListDs.queryParameter));

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    setOpeLoading(true);
    const res = await logListDs.query(page);
    const records = mavenStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        logListDs.unshift(...records);
      }
      mavenStore.setOldOptsRecord(logListDs.records);
      mavenStore.setLoadMoreBtn(res.hasNextPage);
      setOpeLoading(false);
      return res;
    } else {
      setOpeLoading(false);
      return false;
    }
  }, [logListDs]);
  async function getOpEventTypeLookup() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.AUTH_OPERATE_TYPE');
    setOpEventTypeLookupData(lookupData);
  }

  const init = () => {
    getOpEventTypeLookup();
    loadData();
  };

  useEffect(() => {
    if (getTabKey === LOG_TAB) {
      init();
    }
  }, [getTabKey, getSelectedMenu]);

  const handleSearch = (params) => {
    Object.entries(params).forEach(o => {
      logListDs.setQueryParameter(o[0], o[1]);
    });
    setFlag(isEmpty(logListDs.queryParameter));
    loadData();
  };

  const timeLineProps = useMemo(() => ({ formatMessage, isMore: getLoadMoreBtn, opEventTypeLookupData, loadData, logListDs }), [getLoadMoreBtn, opEventTypeLookupData, loadData, logListDs]);

  const renderHeaderTool = () => (
    <React.Fragment>
      <div className="product-lib-org-management-log-search">
        <TextField placeholder={formatMessage({ id: 'userName' })} onChange={(value) => handleSearch({ loginName: value })} style={{ marginRight: '0.12rem', width: '1.6rem' }} />
        <RangePicker
          onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : undefined, endDate: dateString[1] ? `${dateString[1]} 23:59:59` : undefined })}
          style={{ width: '2.46rem' }}
        />
        <Select
          placeholder={formatMessage({ id: 'infra.codelib.audit.model.opType' })}
          onChange={(value) => handleSearch({ operateType: value })}
          style={{ marginLeft: '0.12rem', width: '2.2rem' }}
          searchable
          clearButton
        >
          {
            (opEventTypeLookupData || []).map(o => (
              <Option key={o.value} value={o.value}>{o.meaning}</Option>
            ))
          }
        </Select>
      </div>
    </React.Fragment>
  );

  return (
    logListDs.totalCount === 0 && searchParamFlag ? (
      <Content>
        <Empty
          loading={getLoading}
          pic={empty}
          title=""
          description={formatMessage({ id: 'infra.docManage.message.noOperationLog' })}
        />
      </Content>
    ) :
      (
        <div className="product-lib-org-management-log-page" >
          {renderHeaderTool()}
          <TimeLine {...timeLineProps} />
        </div >)
  );
});

export default ListView;
