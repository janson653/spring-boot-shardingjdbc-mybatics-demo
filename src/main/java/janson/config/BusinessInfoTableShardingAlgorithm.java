package janson.config;

import io.shardingsphere.core.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessInfoTableShardingAlgorithm implements ComplexKeysShardingAlgorithm {
	/**
	 * 强制路由表标签，用于sql like查询
	 */
	// flag:condition, use condition find table
	public static final String FLAG_SQL_LIKE_TABLE = "alltable";
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(BusinessInfoTableShardingAlgorithm.class);

	@Override
	public Collection<String> doSharding(Collection<String> collection, Collection<ShardingValue> shardingValues) {
		log.info("shardingvalues = {}", shardingValues);

		// sql in 查询时可能有多个分表
		List<ShardingValue> shardingValueList = shardingValues.stream()
				.filter(shardingValue -> "businessNo".equalsIgnoreCase(shardingValue.getColumnName())).collect(Collectors.toList());
		log.info("shardingValueList={}", shardingValueList);
		for (ShardingValue shardingValue : shardingValueList) {
			ListShardingValue<String> listShardingValue = (ListShardingValue) shardingValue;
			List<String> businessNoList = listShardingValue.getValues().stream().collect(Collectors.toList());
			for (String authNo : businessNoList) {
				// sql like 操作，获取分表名, 模拟实现分表逻辑
				if (authNo.startsWith(FLAG_SQL_LIKE_TABLE)) {
					// 从传入flag中截取分表名

					String[] arr = authNo.split(":");
					if (arr.length <= 1) { // 没传条件，就所有表扫描
						List<String> tableList = new ArrayList<>();
						tableList.add("business_info_330102001");
						tableList.add("business_info_330102003");
						tableList.add("business_info_330103001");
						tableList.add("business_info_330103002");
						tableList.add("business_info_330103003");
						return tableList;
					}

					// 否则查询具体表名
					List<String> tableList = new ArrayList<>();
					tableList.add("business_info_330102001");
					tableList.add("business_info_330102003");
					return tableList;

				}
			}
		}

		return Collections.singleton("business_info_330102001");
	}
}
