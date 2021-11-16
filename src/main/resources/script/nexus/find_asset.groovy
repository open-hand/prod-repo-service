package script.nexus

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet


List<AssetItem> assets = new ArrayList<>()

def param = new JsonSlurper().parseText(args)
if (param.path == null) {
    return JsonOutput.toJson(assetItem)
}
Repository repo = repository.repositoryManager.get(param.repositoryName)

def format = 'yyyy-MM-dd HH:mm:ss'

log.debug("GET {} : {}", repo.getName(), param.path);

def tx = repo.facet(StorageFacet).txSupplier().get()


try {
    tx.begin()
    List<Repository> repoList = new ArrayList<>()
    repoList.add(repo)
    Iterable<Asset> reAsset = tx.findAssets(Query.builder().where('name').eq(param.path).build(), repoList)

    reAsset.collect { assetIt ->
        AssetItem assetItem = new AssetItem()
        assetItem.repository = param.repositoryName
        assetItem.id = assetIt.getEntityMetadata().getId() == null ? null : assetIt.getEntityMetadata().getId().value
        assetItem.path = assetIt.name()
        assetItem.lastUpdateDate = assetIt.lastUpdated() == null ? null : assetIt.lastUpdated().toDate().format(format)
        assetItem.format = assetIt.format()
        assetItem.componentId = assetIt.componentId() == null ? null : assetIt.componentId().value
        assetItem.lastDownloadDate = assetIt.lastDownloaded() == null ? null : assetIt.lastDownloaded().toDate().format(format)
        assetItem.createdBy = assetIt.createdBy()
        assetItem.createdByIp = assetIt.createdByIp()
        assetItem.size=assetIt.size()
        assets.add(assetItem)
    }
    tx.commit()
} finally {
    tx.close()
}
return JsonOutput.toJson(assets)