import React from 'react';
import { StoreProvider } from './stores';
import MavenContent from './MavenContent';

export default (props) => (
  <StoreProvider value={props}>
    <MavenContent />
  </StoreProvider>
);
