package janson.config;


import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.shardingsphere.core.api.ShardingDataSourceFactory;
import janson.config.property.ShardingConfigProperties;
import janson.config.property.ShardingDatasourceProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.mapper.autoconfigure.SpringBootVFS;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: Zhang Qian
 * @date: 2018/6/15 13:46
 * Description: ShardingDataSource配置类, 包括shardingDataSource创建, mybatis配置
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({ShardingDatasourceProperties.class, ShardingConfigProperties.class})
@MapperScan(basePackages = {"janson.mapper"}, sqlSessionFactoryRef = "shardingSqlSessionFactory")
public class ShardingDatasourceConfiguration {

	public static final String SEPARATOR = ",";
	// 精确设置数据源目录，以便跟其他数据源隔离
	public static String TYPE_ALIASES_PACKAGE = "janson.entity";
	public static String[] MAPPER_LOCATIONS = {"classpath*:mapper/janson/*.xml"};

	private final Map<String, DataSource> dataSourceMap = Maps.newHashMap();
	@Autowired
	private ShardingDatasourceProperties datasourceProperties;
	@Autowired
	private ShardingConfigProperties shardingProperties;

	@Bean(name = "shardingDataSource")
	public DataSource shardingDataSource() throws SQLException {
		String dataSourceName = shardingProperties.getDefaultDataSourceName();
		Splitter.on(SEPARATOR).splitToList(dataSourceName).forEach(name -> {
			if (datasourceProperties.getDataSources().containsKey(name)) {
				DataSourceProperties dataSourceProperties = datasourceProperties.getDataSources().get(name);
				dataSourceMap.put(name, dataSourceProperties.initializeDataSourceBuilder().build());
			}
		});

		DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap,
				shardingProperties.getShardingRuleConfiguration(), shardingProperties.getConfigMap(), shardingProperties.getProps());
		return dataSource;
	}

	@Bean(name = "shardingTransactionManager")
	public DataSourceTransactionManager masterTransactionManager() throws SQLException {
		return new DataSourceTransactionManager(shardingDataSource());
	}

	@Bean(name = "shardingSqlSessionFactory")
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("shardingDataSource") DataSource shardingDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(shardingDataSource);
		sessionFactory.setVfs(SpringBootVFS.class);
		sessionFactory.setMapperLocations(resolveMapperLocations(MAPPER_LOCATIONS));
		sessionFactory.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
		return sessionFactory.getObject();
	}

	private Resource[] resolveMapperLocations(String[] locations) {
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		List<Resource> resources = new ArrayList<Resource>();
		if (locations != null) {
			for (String mapperLocation : locations) {
				try {
					Resource[] mappers = resourceResolver.getResources(mapperLocation);
					resources.addAll(Arrays.asList(mappers));
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return resources.toArray(new Resource[resources.size()]);
	}
}
