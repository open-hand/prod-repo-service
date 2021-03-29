import React, { useMemo } from 'react';
import { StatusTag } from '@choerodon/components';
import { Table } from 'choerodon-ui/pro';
import ClickText from '@/components/click-text';

const { Column } = Table;

const prefixCls = 'c7ncd-mirrorScanning';

const ScanReprot = ({
  dockerImageScanDetailsDs,
}) => {
  const statusMap = useMemo(() => new Map([
    ['UNKNOWN', { code: 'unready', name: '未知' }],
    ['LOW', { code: 'running', name: '较低' }],
    ['MEDIUM', { code: 'opened', name: '中等' }],
    ['HIGH', { code: 'error', name: '严重' }],
    ['CRITICAL', { code: 'disconnect', name: '危机' }],
  ]), []);

  function handleLink(vulnerabilityCode) {
    window.open(`https://cve.mitre.org/cgi-bin/cvename.cgi?name=${vulnerabilityCode}`);
  }

  const renderStatus = ({ text }) => {
    const { code, name } = statusMap.get(text) || {};
    return <StatusTag colorCode={code} type="border" name={name} />;
  };

  const renderExpandRow = ({ record }) => {
    const text = record.get('description');
    return (
      <div className={`${prefixCls}-table-describe`}>
        <p>
          简介：
          {text}
        </p>
      </div>
    );
  };
  return (
    <Table
      dataSet={dockerImageScanDetailsDs}
      mode="tree"
      queryBar="none"
      expandedRowRenderer={renderExpandRow}
      className={`${prefixCls}-table`}
    >
      <Column
        name="vulnerabilityCode"
        renderer={({ text, record }) => (
          <ClickText
            clickAble
            onClick={() => handleLink(record.get('vulnerabilityCode'))}
            value={text}
          />
        )}
        sortable
      />
      <Column name="severity" renderer={renderStatus} width={100} sortable />
      <Column name="pkgName" sortable />
      <Column name="installedVersion" sortable />
      <Column name="fixedVersion" sortable />
    </Table>
  );
};

export default ScanReprot;
