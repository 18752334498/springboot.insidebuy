在@Query中编写JPQL语句进行update或者delete时，必须使用@Modifying注解，以通知SpringData这是一个update或者delete操作。

在update或者delete操作时，需要使用事务；此时需要在Service实现类的方法上声明事务@Transactional。

Spring Data 提供了默认的事务处理方式，即所有的查询均声明为只读事务。

对于自定义的方法，如需改变 Spring Data 提供的事务默认方式，可以在方法上注解 @Transactional 声明 。

进行多个 Repository 操作时，也应该使它们在同一个事务中处理，按照分层架构的思想，这部分属于业务逻辑层，因此，需要在 Service 层实现对多个 Repository 的调用，并在相应的方法上声明事务。


Consider defining a bean named 'entityManagerFactory' in your configuration.
hibernate和jpa的jar包冲突了
删除 \repository\org\hibernate\hibernate-core目录下的文件，然后maven - update project即可

事务管理只有在service加上事务管理才起作用,query不需要事务管理但是delete update modify都需要事务管理。
为了不在service层不加事务管理可以在repository层的delete update modify加上@transactional 但这样不能真正保持事务的完整性