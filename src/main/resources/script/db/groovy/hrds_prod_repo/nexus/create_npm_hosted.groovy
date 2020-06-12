package script.db.groovy.hrds_prod_repo.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.manager.RepositoryManager
import org.sonatype.nexus.repository.maven.LayoutPolicy
import org.sonatype.nexus.repository.maven.VersionPolicy
import org.sonatype.nexus.repository.storage.WritePolicy

def param = new JsonSlurper().parseText(args)

RepositoryManager repositoryManager = repository.repositoryManager

Repository existingRepository = repositoryManager.get(param.name)

if (existingRepository != null) {

  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['storage']['writePolicy'] = param.storage.writePolicy.toUpperCase()
  repositoryManager.update(newConfig)
} else {
  WritePolicy writePolicy = Enum.valueOf(WritePolicy.class, param.storage.writePolicy.toUpperCase())
  repository.createNpmHosted(param.name, param.storage.blobStoreName, true, writePolicy)

}