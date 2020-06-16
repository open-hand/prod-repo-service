package script.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.manager.RepositoryManager

def param = new JsonSlurper().parseText(args)

def RepositoryManager repositoryManager = repository.repositoryManager

def Repository existingRepository = repositoryManager.get(param.name)

if (existingRepository != null) {
  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['group']['memberNames'] = param.members
  newConfig.attributes['storage']['strictContentTypeValidation'] = true
  repositoryManager.update(newConfig)
} else {
  repository.createNpmGroup(param.name, param.members, param.blobStoreName)
}