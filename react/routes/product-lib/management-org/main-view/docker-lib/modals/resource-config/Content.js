import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, TextField, NumberField, Select, Spin } from 'choerodon-ui/pro';
import { useConfigStore } from './stores';
import './index.less';

export default observer(() => {
  const {
    formDs,
    modal,
    refresh,
    repoName,
    formatMessage,
    intlPrefix,
  } = useConfigStore();

  async function handleCreate() {
    try {
      if ((await formDs.submit()) !== false) {
        refresh();
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }

  modal.handleOk(handleCreate);

  return (
    <Spin spinning={formDs.status === 'loading'} className="infra-prod-lib-org-resource-config">
      <Form >
        <TextField label={formatMessage({ id: `${intlPrefix}.model.mirrorLibName` })} name="repoName" value={repoName} disabled />
      </Form>
      <Form dataSet={formDs} columns={8}>
        <NumberField
          colSpan={8}
          name="countLimit"
          style={{
            width: '100%',
          }}
        />
        <NumberField colSpan={6} name="storageNum" style={{ width: '2.5rem' }} />
        <Select
          name="storageUnit"
          colSpan={2}
          searchable
          clearButton
        />
      </Form>
    </Spin>
  );
});
