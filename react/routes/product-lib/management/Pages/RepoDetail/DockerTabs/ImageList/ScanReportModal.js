import React, { useEffect } from 'react';
import { Table, Pagination } from 'choerodon-ui/pro';
import ClickText from '@/components/click-text';

const { Column } = Table;

const prefixCls = 'c7ncd-mirrorScanning';

const ScanReprot = ({
  dockerImageScanDetailsDs, rendererTag: renderStatus, tagName, digest, repoName,
}) => {
  useEffect(() => {
    dockerImageScanDetailsDs.setQueryParameter('tagName', tagName);
    dockerImageScanDetailsDs.setQueryParameter('digest', digest);
    dockerImageScanDetailsDs.setQueryParameter('repoName', repoName);
    dockerImageScanDetailsDs.query();
  }, []);

  function handleLink(vulnerabilityCode) {
    window.open(`https://cve.mitre.org/cgi-bin/cvename.cgi?name=${vulnerabilityCode}`);
  }

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
    <div>
      <Table
        dataSet={dockerImageScanDetailsDs}
        mode="tree"
        queryBar="none"
        expandedRowRenderer={renderExpandRow}
        className={`${prefixCls}-table`}
      >
        <Column
          name="id"
          renderer={({ text, record }) => (
            <ClickText
              clickAble
              onClick={() => handleLink(record.get('id'))}
              value={text}
            />
          )}
          sortable
        />
        <Column name="severity" renderer={renderStatus} width={100} sortable />
        <Column name="packageStr" sortable />
        <Column name="version" sortable />
        <Column name="fixVersion" sortable />
      </Table>
    </div>
  );
};

export default ScanReprot;
