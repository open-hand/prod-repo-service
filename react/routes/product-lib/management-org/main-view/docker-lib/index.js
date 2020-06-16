import React from 'react';
import { StoreProvider } from './stores';
import DockerContent from './DockerContent';

export default (props) => (
  <StoreProvider value={props}>
    <DockerContent />
  </StoreProvider>
);
