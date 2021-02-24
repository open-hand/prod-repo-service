import { useState, useEffect, useCallback } from 'react';
import { axios, stores } from '@choerodon/boot';

const useRepoList = () => {
  const [repoList, setRepoList] = useState([]);
  const [createdRepoList, setCreatedRepoList] = useState([]);

  const fetchRepo = useCallback(async (params = {}) => {
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    try {
      const res = await axios.get(`/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/npm/repo/group`, {
        params,
      });
      setRepoList([...res]);
    } catch (error) {
      // message.error(error);
    }
  }, [stores.AppState]);

  useEffect(() => {
    fetchRepo();
  }, [fetchRepo]);

  return { repoList, createdRepoList, setCreatedRepoList };
};

export default useRepoList;
