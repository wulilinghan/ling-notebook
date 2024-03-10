# 一：什么是liquibase

LiquiBase（从 2006 年开始投入使用）是一种免费开源的工具，可以实现不同数据库版本之间的迁移（参见 [参考资料](https://blog.csdn.net/weixin_41404773/article/details/106355563#artrelatedtopics)）。目前也存在少量其他开源数据库迁移工具，包括 openDBcopy 和 dbdeploy。LiquiBase 支持 10 种数据库类型，包括 DB2、Apache Derby、MySQL、PostgreSQL、Oracle、Microsoft®SQL Server、Sybase 和 HSQL。

通过日志文件的形式记录数据库的变更，然后执行日志文件中的修改，将数据库更新或回滚到一致的状态。它的目标是提供一种数据库类型无关的解决方案，通过执行schema类型的文件来达到迁移。