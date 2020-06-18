package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_repository.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2020-04-27-rdupm_harbor_repository") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_repository_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_repository", remarks: "制品库-harbor镜像仓库表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "主键")  {constraints(primaryKey: true)} 
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "code", type: "varchar(" + 100 * weight + ")",  remarks: "项目编码")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 100 * weight + ")",  remarks: "项目名称")  {constraints(nullable:"false")}  
            column(name: "public_flag", type: "varchar(" + 10 * weight + ")",  remarks: "是否公开访问，默认false")   
            column(name: "harbor_id", type: "bigint(20)",  remarks: "harbor项目ID")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }
   createIndex(tableName: "rdupm_harbor_repository", indexName: "rdupm_harbor_repository_N1") {
            column(name: "organization_id")
        }

        addUniqueConstraint(columnNames:"code",tableName:"rdupm_harbor_repository",constraintName: "code")
        addUniqueConstraint(columnNames:"project_id",tableName:"rdupm_harbor_repository",constraintName: "project_id")
    }
}