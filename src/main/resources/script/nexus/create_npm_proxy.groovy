package org.hrds.rdupm.nexus.client.nexus.script

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.manager.RepositoryManager
import org.sonatype.nexus.repository.maven.LayoutPolicy
import org.sonatype.nexus.repository.maven.VersionPolicy

def param = new JsonSlurper().parseText(args)

RepositoryManager repositoryManager = repository.repositoryManager
authentication = param.remoteUsername == null ? null : [
        type: 'username',
        username: param.remoteUsername,
        password: param.remotePassword
]

Repository existingRepository = repositoryManager.get(param.name)

if (existingRepository != null) {

  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['proxy']['remoteUrl'] = param.remoteUrl
  newConfig.attributes['httpclient']['authentication'] = authentication
  repositoryManager.update(newConfig)
} else {
  existingRepository = repository.createNpmProxy(param.name, param.remoteUrl, param.blobStoreName)
  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['httpclient']['authentication'] = authentication
  repositoryManager.update(newConfig)
}