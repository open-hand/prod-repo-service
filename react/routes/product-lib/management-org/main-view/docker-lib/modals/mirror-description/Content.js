import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form } from 'choerodon-ui/pro';
import './index.less';

export default observer(({ description }) => (
  <div>
    <Form>
      <pre>{description}</pre>
    </Form>
  </div>
));
