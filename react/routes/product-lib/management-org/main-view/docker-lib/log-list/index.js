/**
* 操作日志
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/05/6
* @copyright 2020 ® HAND
*/
import React, {
  useEffect, useMemo, useState, useCallback,
} from 'react';
import { Stores, Select, TextField } from 'choerodon-ui/pro';
import { DatePicker, Radio } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
// import { isEmpty } from 'lodash';
// import { Content } from '@choerodon/boot';
// import Empty from '@/components/empty';
// import empty from '@/assets/empty.png';
import './log.less';
import { useDockerStore } from '../stores';
import { useProdStore } from '../../../stores';
import TimeLine from './TimeLine';

const { RangePicker } = DatePicker;
const { Option } = Select;

const ListView = observer(() => {
  const {
    prodStore: { getSelectedMenu }, formatCommon,
    formatClient,
  } = useProdStore();
  const {
    organizationId,
    formatMessage,
    tabs: {
      LOG_TAB,
    },
    dockerStore,
    logListDs,
  } = useDockerStore();
  const {
    getTabKey, setOpeLoading, getLoadMoreBtn, setLogTabKey, getLogTabKey,
  } = dockerStore;

  const [opEventTypeLookupData, setOpEventTypeLookupData] = useState([]);
  const [opMirrorLibLookupData, setOpMirrorLibLookupData] = useState([]);
  // const [searchParamFlag, setFlag] = useState(false);
  const [defaultMirror, setDefaultMirror] = useState(undefined);

  const handleTabChange = useCallback((e) => {
    setLogTabKey(e.target.value);
  });

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    setOpeLoading(true);
    const res = await logListDs.query(page);
    const records = dockerStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        logListDs.unshift(...records);
      }
      dockerStore.setOldOptsRecord(logListDs.records);
      dockerStore.setLoadMoreBtn(res.hasNextPage);
      setOpeLoading(false);
      return res;
    }
    setOpeLoading(false);
    return false;
  }, [logListDs]);

  async function getOpMirrorLibLookup() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData(`/rdupm/v1/harbor-project/all/${organizationId}`);
    const dataList = lookupData.map((i) => ({ value: i.code, meaning: i.name, id: i.id }));
    logListDs.queryDataSet.records[0].set('projectCode', dataList.length > 0 ? dataList[0].value : undefined);
    setDefaultMirror(dataList.length > 0 ? dataList[0].value : undefined);
    loadData();
    setOpMirrorLibLookupData(dataList);
  }
  async function getOpEventTypeLookup() {
    let lookupData = [];
    if (getLogTabKey === 'AuthLog') {
      lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.AUTH_OPERATE_TYPE');
    } else if (getLogTabKey === 'ImgLog') {
      lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.IMAGE_OPERATE_TYPE');
    }
    setOpEventTypeLookupData(lookupData);
  }

  const init = () => {
    getOpEventTypeLookup();

    if (getLogTabKey === 'ImgLog') {
      getOpMirrorLibLookup();
    } else {
      loadData();
    }
  };

  useEffect(() => {
    if (getTabKey === LOG_TAB) {
      init();
    }
  }, [getTabKey, getSelectedMenu, getLogTabKey]);

  const handleSearch = (params) => {
    Object.entries(params).forEach((o) => {
      if (o[0] === 'projectCode') {
        setDefaultMirror(o[1]);
      }
      logListDs.setQueryParameter(o[0], o[1]);
    });
    // setFlag(isEmpty(logListDs.queryParameter));
    loadData();
  };

  const timeLineProps = useMemo(() => ({
    formatMessage, isMore: getLoadMoreBtn, opEventTypeLookupData, loadData, logListDs,
  }), [getLoadMoreBtn, opEventTypeLookupData, loadData, logListDs]);

  const renderHeaderTool = () => (
    <>
      <Radio.Group
        value={getLogTabKey}
        onChange={handleTabChange}
        className="product-lib-org-management-log-search-radio"
      >
        <Radio.Button value="AuthLog">
          {formatClient({ id: 'docker.log.permissionOperationRecord' })}
        </Radio.Button>
        <Radio.Button value="ImgLog">
          {formatClient({ id: 'docker.log.imageOperationRecord' })}
        </Radio.Button>
      </Radio.Group>
      <div className="product-lib-org-management-log-search">
        {getLogTabKey === 'ImgLog' && (
          <>
            <Select
              searchable
              clearButton
              value={defaultMirror}
              placeholder={formatMessage({ id: 'infra.prod.lib.view.mirrorLib' })}
              onChange={(value) => handleSearch({ projectCode: value })}
              style={{ marginRight: '0.12rem', width: '3.35rem' }}
            >
              {
                (opMirrorLibLookupData || []).map((o) => (
                  <Option key={o.value} value={o.value}>
                    {`${o.meaning}(${o.value})`}
                  </Option>
                ))
              }
            </Select>
            <TextField placeholder={formatMessage({ id: 'infra.prod.lib.model.imageName' })} onChange={(value) => handleSearch({ imageName: value })} style={{ marginRight: '0.12rem', width: '1.6rem' }} />
            <RangePicker
              onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : undefined, endDate: dateString[1] ? `${dateString[1]} 23:59:59` : undefined })}
              style={{ width: '2.46rem' }}
            />
            <Select
              searchable
              clearButton
              placeholder={formatMessage({ id: 'infra.codelib.audit.model.opType' })}
              onChange={(value) => handleSearch({ operateType: value })}
              style={{ marginLeft: '0.12rem', width: '2.2rem' }}
            >
              {
                (opEventTypeLookupData || []).map((o) => (
                  <Option key={o.value} value={o.value}>{o.meaning}</Option>
                ))
              }
            </Select>
          </>
        )}
        {getLogTabKey === 'AuthLog' && (
          <>
            <TextField placeholder={formatClient({ id: 'docker.log.userName' })} onChange={(value) => handleSearch({ loginName: value })} style={{ marginRight: '0.12rem', width: '1.6rem' }} />
            <RangePicker
              onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : undefined, endDate: dateString[1] ? `${dateString[1]} 23:59:59` : undefined })}
              style={{ width: '2.46rem' }}
            />
            <Select
              searchable
              clearButton
              placeholder={formatClient({ id: 'docker.log.operationType' })}
              onChange={(value) => handleSearch({ operateType: value })}
              style={{ marginLeft: '0.12rem', width: '2.2rem' }}
            >
              {
                (opEventTypeLookupData || []).map((o) => (
                  <Option key={o.value} value={o.value}>{o.meaning}</Option>
                ))
              }
            </Select>
          </>
        )}
      </div>
    </>
  );

  return (
    // logListDs.totalCount === 0 && searchParamFlag ? (
    //   <Content>
    //     <Empty
    //       loading={getLoading}
    //       pic={empty}
    //       title=""
    //       description={formatMessage({ id: 'infra.docManage.message.noOperationLog' })}
    //     />
    //   </Content>
    // ) :
    //   (
    <div className="product-lib-org-management-log-page">
      {renderHeaderTool()}
      <TimeLine {...timeLineProps} />
    </div>
    // )
  );
});

export default ListView;
