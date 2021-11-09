package script.nexus

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.StorageFacet


AssetItem assetItem = new AssetItem()

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
    Asset asset = tx.findAssetWithProperty("name", param.path)

    assetItem.id = asset.getEntityMetadata().getId() == null ? null : asset.getEntityMetadata().getId().value
    assetItem.path = asset.name()
    assetItem.lastUpdateDate = asset.lastUpdated() == null ? null : asset.lastUpdated().toDate().format(format)
    assetItem.format = asset.format()
    assetItem.componentId = asset.componentId() == null ? null : asset.componentId().value
    assetItem.lastDownloadDate = asset.lastDownloaded() == null ? null : asset.lastDownloaded().toDate().format(format)
    assetItem.createdBy = asset.createdBy()
    assetItem.createdByIp = asset.createdByIp()
    assetItem.size=asset.size()
    tx.commit()
} finally {
    tx.close()
}
return JsonOutput.toJson(assetItem)




