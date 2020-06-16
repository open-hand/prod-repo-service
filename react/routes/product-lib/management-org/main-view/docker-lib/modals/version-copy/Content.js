import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, TextField } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
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
        <Select name="destProjectCode" />
        <TextField name="destImageName" />
        <TextField name="destImageTagName" />
      </Form>
    </div>
  );
});
