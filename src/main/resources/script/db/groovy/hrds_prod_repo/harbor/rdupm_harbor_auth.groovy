package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_auth.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2020-04-27-rdupm_harbor_auth") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_auth_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_auth", remarks: "制品库-harbor权限表") {
            column(name: "auth_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint(20)",  remarks: "猪齿鱼用户ID")  {constraints(nullable:"false")}  
            column(name: "login_name", type: "varchar(" + 100 * weight + ")",  remarks: "登录名")   
            column(name: "real_name", type: "varchar(" + 100 * weight + ")",  remarks: "")   
            column(name: "harbor_role_id", type: "bigint(20)",  remarks: "harbor角色ID")  {constraints(nullable:"false")}  
            column(name: "harbor_auth_id", type: "bigint(20)",  remarks: "harbor权限ID")  {constraints(nullable:"false")}  
            column(name: "end_date", type: "datetime",  remarks: "有效期")  {constraints(nullable:"false")}
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织ID")  {constraints(nullable:"false")}
            column(name: "locked", type: "varchar(" + 10 * weight + ")",  remarks: "锁定标记")
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"project_id,user_id",tableName:"rdupm_harbor_auth",constraintName: "project_id")
    }

    changeSet(id: '2020-09-29-rdupm_harbor_auth-update', author: 'weisen.yang@hand-china.com') {
        modifyDataType(tableName: 'rdupm_harbor_auth', columnName: 'created_by', newDataType: 'bigint(20)')
        sql("alter table rdupm_harbor_auth modify created_by bigint(20)")
        sql("alter table rdupm_harbor_auth modify last_updated_by bigint(20)")
        sql("alter table rdupm_harbor_auth modify last_update_login bigint(20)")
    }
}