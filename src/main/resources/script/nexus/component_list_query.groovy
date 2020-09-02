package script.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.config.Configuration
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Component
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

import groovy.json.JsonOutput

Result result = new Result()
result.items = new ArrayList()

// 参数
def param = new JsonSlurper().parseText(args)
if (param.repositoryName == null) {
  return JsonOutput.toJson(result)
}
def format = 'yyyy-MM-dd HH:mm:ss'

Repository repo = repository.repositoryManager.get(param.repositoryName)
if (repo != null) {
  List<Repository> repoList = new ArrayList<>()
  addRepo(repo, repoList)

  def tx = repo.facet(StorageFacet).txSupplier().get()
  try {
    tx.begin()

    Query.Builder queryBuilder = Query.builder()
    queryBuilder.where(' 1=1')

    if (param.name != null) {
      queryBuilder.and(' name like ').param(spliceLike(param.name))
    }
    if (param.version != null) {
      queryBuilder.and(' version like ').param(spliceLike(param.version))
    }
    if (param.group != null) {
      queryBuilder.and(' group like ').param(spliceLike(param.group))
    }

    queryBuilder.suffix(' ORDER BY group, name, version ')
    Iterable<Component> componentIterable = tx.findComponents(queryBuilder.build(), repoList)

    componentIterable.collect {
      def componentItem = new ComponentItem()

      // Component
      componentItem.repository = param.repositoryName
      componentItem.repositoryUrl = repo.getUrl()
      componentItem.id = it.getEntityMetadata().getId() == null ? null : it.getEntityMetadata().getId().value
      componentItem.name = it.name()
      componentItem.group = it.group()
      componentItem.lastUpdateDate = it.lastUpdated() == null ? null : it.lastUpdated().toDate().format(format)
      componentItem.creationDate = componentItem.lastUpdateDate
      componentItem.format = it.format()
      componentItem.version = it.version()

      result.items.add(componentItem)

      // Asset
      componentItem.assets = new ArrayList<>();
      Iterable<Asset> assets = tx.browseAssets(it)
      assets.collect { assetIt ->

        if (componentItem.createdBy == null) {
          // 创建人
          componentItem.createdBy = assetIt.createdBy()
        }

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

        componentItem.assets.add(assetItem)
      }

    }

    tx.commit()
  } finally {
    tx.close()
  }
}


return JsonOutput.toJson(result)


static String spliceLike(String param) {
  return '%' + param + '%'
}

static void addRepo(Repository repo, List<Repository> repoList) {
  if (repo != null) {
    repoList.add(repo)
    // group类型， 处理
    def type = repo.type.getValue()
    if ('group' == type) {
      Configuration configuration = repo.getConfiguration()
      List<String> memberStrList = configuration.attributes['group']['memberNames'] as List<String>
      if (memberStrList != null) {
        memberStrList.collect {
          Repository memberRepo = repository.repositoryManager.get(it)
          addRepo(memberRepo, repoList)
        }
      }
    }
  }
}

// 返回结果类
class Result {
  List<ComponentItem> items;
}

class ComponentItem {
  String id
  String repository
  String repositoryUrl
  String format
  String group
  String name
  String version
  String lastUpdateDate
  String creationDate
  String createdBy
  List<AssetItem> assets
}

class AssetItem {
  String repository
  String id
  String path
  String lastUpdateDate
  String format
  String componentId
  String lastDownloadDate
  String createdBy
  String createdByIp
}