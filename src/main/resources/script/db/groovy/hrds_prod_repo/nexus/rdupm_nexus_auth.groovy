package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_auth.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_auth") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_auth_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_auth", remarks: "制品库_nexus权限表") {
            column(name: "auth_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "project_id", type: "bigint",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint",  remarks: "组织ID")  {constraints(nullable:"false")}  
            column(name: "repository_id", type: "bigint",  remarks: "rdupm_nexus_repository 表主键")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "猪齿鱼用户ID")  {constraints(nullable:"false")}  
            column(name: "login_name", type: "varchar(" + 100 * weight + ")",  remarks: "登录名")   
            column(name: "real_name", type: "varchar(" + 100 * weight + ")",  remarks: "")   
            column(name: "role_code", type: "varchar(" + 30 * weight + ")",  remarks: "角色编码")  {constraints(nullable:"false")}  
            column(name: "ne_role_id", type: "varchar(" + 100 * weight + ")",  remarks: "用户对应nexus角色Id")  {constraints(nullable:"false")}
            column(name: "locked", type: "varchar(" + 10 * weight + ")",  remarks: "锁定标记")
            column(name: "end_date", type: "datetime",  remarks: "有效期")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_auth", indexName: "rdupm_nexus_auth_N1") {
            column(name: "repository_id")
        }
   createIndex(tableName: "rdupm_nexus_auth", indexName: "rdupm_nexus_auth_N2") {
            column(name: "user_id")
        }
   createIndex(tableName: "rdupm_nexus_auth", indexName: "rdupm_nexus_auth_N3") {
            column(name: "project_id")
            column(name: "organization_id")
        }

    }
}