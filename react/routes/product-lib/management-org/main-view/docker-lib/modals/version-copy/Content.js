import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, TextField } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { NewTips } from '@choerodon/components';
import { useVersionCopyStore } from './stores';

export default observer(() => {
  const {
    copyFormDs,
    modal,
    refresh,
    refreshMirrorList,
  } = useVersionCopyStore();
  modal.handleOk(async () => {
    try {
      if (await copyFormDs.submit() !== false) {
        refresh();
        refreshMirrorList();
        return true;
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  });

  modal.handleCancel(() => {
    copyFormDs.reset();
  });

  return (
    <div>
      <Form dataSet={copyFormDs}>
        <Select
          name="destProjectCode"
          searchable
          clearButton
        />
        <TextField name="destImageName" />
        <TextField
          name="destImageTagName"
          addonAfter={
            <NewTips helpText="若您使用的是V1.x 的Harbor，则此处为必填；若使用的是V2.x的Harbor，此处请不要填写。" />
          }
        />
      </Form>
    </div>
  );
});
