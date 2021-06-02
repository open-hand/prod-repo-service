/* eslint-disable */
import React, { useMemo, Fragment } from 'react';
import PropTypes from 'prop-types';
import groupBy from 'lodash/groupBy';
import initial from 'lodash/initial';
import flatten from 'lodash/flatten';
import map from 'lodash/map';
import { Button } from 'choerodon-ui/pro';
import { Permission } from '@choerodon/boot';
// import { Button, Tooltip } from 'choerodon-ui/pro';
import { Tooltip, Divider } from 'choerodon-ui';

import './index.less';

const HeaderButtons = ({ items, children }) => {
  const displayBtn = useMemo(() => items.filter(({ display }) => display), [items]);

  const btnNodes = useMemo(() => {
    const btnGroups = map(groupBy(displayBtn, 'group'), (value) => {
      const Split = <Divider key={Math.random()} type="vertical" className="c7ncd-header-split" />;

      const btns = map(value, ({
        name, handler, permissions, display, disabled, disabledMessage, ...props
      }) => {
        const btn = (
          <Button
            {...props}
            disabled={disabled}
            className="c7ncd-header-btn"
            onClick={handler}
          >
            {name}
          </Button>
        );
        return (
          <Fragment key={name}>
            {permissions && permissions.length ? (
              <Permission service={permissions}>
                {disabled && disabledMessage ? (
                  <Tooltip title={disabledMessage || ''} placement="bottom">
                    {btn}
                  </Tooltip>
                ) : btn}
              </Permission>
            ) : btn}
          </Fragment>
        );
      });

      return [...btns, Split];
    });

    return initial(flatten(btnGroups));
  }, [displayBtn]);

  return displayBtn.length ? (
    <div className="c7ncd-header-btns">
      {btnNodes}
      {children}
    </div>
  ) : null;
};

HeaderButtons.propTypes = {
  items: PropTypes.array,
};

HeaderButtons.defaultProps = {
  items: [],
};

export default HeaderButtons;
