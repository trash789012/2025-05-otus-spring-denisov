package ru.otus.hw.config.acl;

//@Configuration
//@RequiredArgsConstructor
public class AclConfig {

//    private final DataSource dataSource;

//    private final CacheManager cacheManager;

//    @Autowired
//    public AclConfig(DataSource dataSource, CacheManager cacheManager) {
//        this.dataSource = dataSource;
//        this.cacheManager = cacheManager;
//    }

//    @Bean
//    public SpringCacheBasedAclCache aclCache() {
//        return new SpringCacheBasedAclCache(
//                cacheManager.getCache("aclCache"),
//                permissionGrantingStrategy(),
//                aclAuthorizationStrategy()
//        );
//    }
//
//    @Bean
//    public PermissionGrantingStrategy permissionGrantingStrategy() {
//        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
//    }
//
//    @Bean
//    public AclAuthorizationStrategy aclAuthorizationStrategy() {
//        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
//    }
//
//    @Bean
//    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService()));
//        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
//        return expressionHandler;
//    }
//
//    @Bean
//    public LookupStrategy lookupStrategy() {
//        return new BasicLookupStrategy(
//                dataSource,
//                aclCache(),
//                aclAuthorizationStrategy(),
//                new ConsoleAuditLogger()
//        );
//    }
//
//    @Bean
//    public JdbcMutableAclService aclService() {
//        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
//    }
}

