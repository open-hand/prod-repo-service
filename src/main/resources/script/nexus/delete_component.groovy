package script.nexus

import groovy.json.JsonSlurper
import org.sonatype.nexus.common.entity.EntityId
import org.sonatype.nexus.repository.IllegalOperationException
import org.sonatype.nexus.repository.MissingFacetException
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.storage.Component
import org.sonatype.nexus.repository.storage.ComponentMaintenance
import org.sonatype.nexus.repository.storage.StorageFacet

// 参数
def param = new JsonSlurper().parseText(args)
List<String> components = param.components
if (components == null || components.size() == 0) {
  return
}

List<Component> componentsList = new ArrayList<>()
// 1. 查询
Repository repo = repository.repositoryManager.get(param.repositoryName)
StorageFacet storageFacet = repo.facet(StorageFacet)
if (repo != null) {
  def tx = storageFacet.txSupplier().get()
  try {
    tx.begin()
    components.collect {
      EntityId entityId = new EntityId() {
        @Override
        String getValue() {
          return it
        }
      }
      Component component = tx.findComponent(entityId)
      if (component != null) {
        componentsList.add(component)
      }
    }
    tx.commit()
  } catch (Exception e) {
    tx.rollback()
    throw new Exception(e)
  } finally {
    if (tx != null) {
      tx.close()
    }
  }
}
// 2. 删除
if (componentsList.size() > 0) {
  componentsList.collect {
    getComponentMaintenanceFacet(repo).deleteComponent(it.getEntityMetadata().getId());
  }
}

private ComponentMaintenance getComponentMaintenanceFacet(final Repository repository) {
  try {
    return repository.facet(ComponentMaintenance.class);
  }
  catch (MissingFacetException e) {
    throw new IllegalOperationException(
            format('Deleting from repository %s of type %s is not supported', repository.getName(),
                    repository.getFormat()), e);
  }
}