package janson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"janson.*"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class MybaticsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybaticsDemoApplication.class, args);
	}
}
