#!/usr/bin/env bash
mkdir -p target
if [ ! -f target/choerodon-tool-liquibase.jar ]
then
    curl https://nexus.choerodon.com.cn/repository/choerodon-release/io/choerodon/choerodon-tool-liquibase/0.9.2.RELEASE/choerodon-tool-liquibase-0.9.2.RELEASE.jar -o target/choerodon-tool-liquibase.jar
fi

java -Dspring.datasource.url="jdbc:mysql://118.25.175.161:3306/hrds_doc_repo_2?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
	 -Dspring.datasource.username=admin \
	 -Dspring.datasource.password=Admin@1234 \
	 -Ddata.drop=false -Ddata.init=init \
	 -Ddata.dir=src/main/resources/script/test \
	 -jar target/choerodon-tool-liquibase.jar



