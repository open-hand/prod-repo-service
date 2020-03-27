package script.db

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_repository.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-03-27-rdupm_nexus_repository") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_repository_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_repository", remarks: "制品库_nexus仓库信息表") {
            column(name: "repository_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "config_id", type: "bigint(20)",  remarks: "nexus服务配置ID: rdupm_nexus_server_config主键")  {constraints(nullable:"false")}  
            column(name: "ne_repository_name", type: "varchar(" + 100 * weight + ")",  remarks: "nexus仓库名称")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织Id")  {constraints(nullable:"false")}  
            column(name: "project_id", type: "bigint(20)",  remarks: "项目id")  {constraints(nullable:"false")}  
            column(name: "allow_anonymous", type: "tinyint(1)",  remarks: "是否允许匿名。1 允许；0 不允许")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户Id")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"ne_repository_name",tableName:"rdupm_nexus_repository",constraintName: "ne_repository_name")
    }
}