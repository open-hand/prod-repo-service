import React from 'react';
import { StoreProvider } from './stores';
import Management from './Management';

export default (props) => (
  <StoreProvider {...props}>
    <Management />
  </StoreProvider>
);
