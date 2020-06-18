package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_custom_repo.groovy') {
    changeSet(author: "mofei.li@hand-china.com", id: "2020-06-05-rdupm_harbor_custom_repo") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_custom_repo_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_custom_repo", remarks: "制品库-harbor自定义镜像仓库表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目ID")   
            column(name: "organization_id", type: "bigint(20)",  remarks: "猪齿鱼组织ID")   
            column(name: "repo_name", type: "varchar(" + 100 * weight + ")",  remarks: "自定义镜像仓库名称")  {constraints(nullable:"false")}  
            column(name: "repo_url", type: "varchar(" + 100 * weight + ")",  remarks: "自定义镜像仓库地址")  {constraints(nullable:"false")}  
            column(name: "login_name", type: "varchar(" + 30 * weight + ")",  remarks: "登录名")  {constraints(nullable:"false")}  
            column(name: "password", type: "varchar(" + 100 * weight + ")",  remarks: "密码")  {constraints(nullable:"false")}  
            column(name: "email", type: "varchar(" + 100 * weight + ")",  remarks: "邮箱")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 255 * weight + ")",  remarks: "描述")   
            column(name: "public_flag", type: "varchar(" + 10 * weight + ")",  remarks: "是否公开访问，默认false")  {constraints(nullable:"false")}
            column(name: "project_share", type: "varchar(" + 10 * weight + ")",  remarks: "是否项目下共用，默认false")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "varchar(" + 10 * weight + ")",  remarks: "是否启用")
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }
   createIndex(tableName: "rdupm_harbor_custom_repo", indexName: "rdupm_harbor_custom_repo_N1") {
            column(name: "project_id")
        }

    }
}