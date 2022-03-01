package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_organization_service.groovy') {
    changeSet(author: "hao.wang08@hand-china.com", id: "2022-02-28-rdupm_nexus_organization_service") {
        createTable(tableName: "rdupm_nexus_organization_service", remarks: "制品库-租户与nexus服务关系表") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {
                constraints(primaryKey: true)
            }
            column(name: "config_id", type: "bigint",  remarks: "rdupm_nexus_server_config 表主键")  {
                constraints(nullable:"false")
            }
            column(name: "organization_id", type: "bigint",  remarks: "猪齿鱼组织ID")  {
                constraints(nullable:"false")
            }
            column(name: "enable_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用")  {
                constraints(nullable:"false")
            }
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {
                constraints(nullable:"false")
            }
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {
                constraints(nullable:"false")
            }
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {
                constraints(nullable:"false")
            }
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {
                constraints(nullable:"false")
            }
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {
                constraints(nullable:"false")
            }

        }
        createIndex(tableName: "rdupm_nexus_organization_service", indexName: "rdupm_nexus_organization_id_service_N1") {
            column(name: "organization_id")
        }

        addUniqueConstraint(columnNames:"config_id, organization_id",tableName:"rdupm_nexus_organization_service",constraintName: "config_organization_id")
    }
}