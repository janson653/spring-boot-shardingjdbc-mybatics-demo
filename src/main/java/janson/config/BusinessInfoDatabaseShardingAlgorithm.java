package janson.config;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessInfoDatabaseShardingAlgorithm implements ComplexKeysShardingAlgorithm {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(BusinessInfoDatabaseShardingAlgorithm.class);

	@Override
	public Collection<String> doSharding(Collection<String> collection, Collection<ShardingValue> shardingValues) {
		log.info("shardingvalues = {}", shardingValues);

		// sql in 查询时可能有多个分表
		List<ShardingValue> shardingValueList = shardingValues.stream()
				.filter(shardingValue -> "businessNo".equalsIgnoreCase(shardingValue.getColumnName())).collect(Collectors.toList());
		log.info("shardingValueList={}", shardingValueList);

		return Collections.singleton("dt");
	}
}
