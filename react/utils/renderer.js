import React from 'react';
import { Tooltip } from 'choerodon-ui';

export default function renderFullName({ text }) {
  return (
    <Tooltip title={text} mouseEnterDelay={0.5}>
      {text}
    </Tooltip>
  );
}
