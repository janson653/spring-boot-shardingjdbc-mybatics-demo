package janson.config.property;

import io.shardingsphere.core.yaml.sharding.YamlShardingRuleConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sharding.jdbc.config.sharding")
public class ShardingConfigProperties extends YamlShardingRuleConfiguration {
}
