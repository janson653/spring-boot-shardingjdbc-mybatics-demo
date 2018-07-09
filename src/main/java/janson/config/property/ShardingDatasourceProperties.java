package janson.config.property;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "sharding.jdbc")
public class ShardingDatasourceProperties {


	private Map<String, DataSourceProperties> dataSources;

	public ShardingDatasourceProperties() {
	}

	public Map<String, DataSourceProperties> getDataSources() {
		return this.dataSources;
	}

	public void setDataSources(Map<String, DataSourceProperties> dataSources) {
		this.dataSources = dataSources;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof ShardingDatasourceProperties)) return false;
		final ShardingDatasourceProperties other = (ShardingDatasourceProperties) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$dataSources = this.getDataSources();
		final Object other$dataSources = other.getDataSources();
		if (this$dataSources == null ? other$dataSources != null : !this$dataSources.equals(other$dataSources))
			return false;
		return true;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $dataSources = this.getDataSources();
		result = result * PRIME + ($dataSources == null ? 43 : $dataSources.hashCode());
		return result;
	}

	protected boolean canEqual(Object other) {
		return other instanceof ShardingDatasourceProperties;
	}

	public String toString() {
		return "ShardingDatasourceProperties(dataSources=" + this.getDataSources() + ")";
	}
}
