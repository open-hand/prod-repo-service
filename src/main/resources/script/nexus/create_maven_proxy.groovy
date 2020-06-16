package script.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.manager.RepositoryManager
import org.sonatype.nexus.repository.maven.LayoutPolicy
import org.sonatype.nexus.repository.maven.VersionPolicy

def param = new JsonSlurper().parseText(args)

RepositoryManager repositoryManager = repository.repositoryManager
// parsed_args
authentication = param.remoteUsername == null ? null : [
        type: 'username',
        username: param.remoteUsername,
        password: param.remotePassword
]

Repository existingRepository = repositoryManager.get(param.name)

if (existingRepository != null) {

  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['maven']['versionPolicy'] = param.versionPolicy.toUpperCase()
  newConfig.attributes['maven']['layoutPolicy'] = param.layoutPolicy.toUpperCase()
  newConfig.attributes['proxy']['remoteUrl'] = param.remoteUrl
  newConfig.attributes['httpclient']['authentication'] = authentication
  newConfig.attributes['storage']['strictContentTypeValidation'] = Boolean.valueOf(param.strictContentValidation)

  repositoryManager.update(newConfig)

} else {
  LayoutPolicy layoutPolicy = Enum.valueOf(LayoutPolicy.class, param.layoutPolicy.toUpperCase())
  VersionPolicy versionPolicy = Enum.valueOf(VersionPolicy.class, param.versionPolicy.toUpperCase())
  existingRepository = repository.createMavenProxy(param.name, param.remoteUrl, param.blobStoreName, Boolean.valueOf(param.strictContentValidation), versionPolicy, layoutPolicy);
  newConfig = existingRepository.configuration.copy()
  newConfig.attributes['httpclient']['authentication'] = authentication
  repositoryManager.update(newConfig)
}