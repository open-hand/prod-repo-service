import React from 'react';
import { StoreProvider } from './stores';
import NpmContent from './NpmContent';

export default (props) => (
  <StoreProvider value={props}>
    <NpmContent />
  </StoreProvider>
);
