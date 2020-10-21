package script.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.config.Configuration
import org.sonatype.nexus.repository.manager.RepositoryManager
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Component
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

import groovy.json.JsonOutput


// 参数
def param = new JsonSlurper().parseText(args)
long result = 0;
if (param.repositoryName == null) {
  return result;
}

Repository repo = repository.repositoryManager.get(param.repositoryName)
if (repo != null) {
  def type = repo.type.getValue()
  if ('hosted' == type) {
    List<Repository> repoList = new ArrayList<>()
    repoList.add(repo)
    def tx = repo.facet(StorageFacet).txSupplier().get()
    try {
      tx.begin()
      Query.Builder queryBuilder = Query.builder()
      result = tx.countComponents(queryBuilder.build(), repoList)
      tx.commit()
    } finally {
      tx.close()
    }
  }
}
return result