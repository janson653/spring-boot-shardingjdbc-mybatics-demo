package shardingjdbc;

import com.github.pagehelper.PageHelper;
import io.shardingsphere.core.api.HintManager;
import janson.MybaticsDemoApplication;
import janson.entity.BusinessInfo;
import janson.mapper.BusinessInfoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author: Zhang Qian
 * @date: 2018/7/5 18:33
 * Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybaticsDemoApplication.class)
public class ShardingJdbcTest {
	@Autowired
	BusinessInfoMapper businessInfoMapper;

	private BusinessInfo mockUpData() {
		BusinessInfo info = new BusinessInfo();
		info.setBusinessNo("330108002-0017-0118");
		return info;
	}

	// 测试单表插入
	@Test
	public void testInsert() {
		StopWatch stopWatch = new StopWatch("insert");
		stopWatch.start();

		BusinessInfo info = mockUpData();
		businessInfoMapper.insert(info);

		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());

		BusinessInfo record = businessInfoMapper.selectOne(info);
		Assert.assertEquals(info, record);
	}

	// 测试单表update
	@Test
	public void testUpdate() {
		StopWatch stopWatch = new StopWatch("update");
		stopWatch.start();

		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());
	}

	// 测试单表query
	@Test
	public void testSingleTableQuery() {
		StopWatch stopWatch = new StopWatch("singleTableQuery");
		stopWatch.start();

		// 普通查询
		BusinessInfo info = mockUpData();
		businessInfoMapper.select(info);

		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());
	}

	// 测试单表query，单表like查询
	@Test
	public void testSingleTableQuery2() {
		StopWatch stopWatch = new StopWatch("singleTableQuery");
		stopWatch.start();


		// like 不模糊
		HintManager hintManager = HintManager.getInstance();
		hintManager.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager.addTableShardingValue("business_info", "businessNo", "single");

		// 不算count了
		businessInfoMapper.myPageListWithLike(1, 100);

		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());
	}

	// 测试单表query
	@Test
	public void testMultiTableQuery() {

		StopWatch stopWatch = new StopWatch("testMultiTableQuery");
		stopWatch.start();


		// like
		HintManager hintManager = HintManager.getInstance();
		hintManager.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager.addTableShardingValue("business_info", "businessNo", "alltable");
		Example example = new Example(BusinessInfo.class);
		LocalDate localDate = LocalDate.of(2018, 4, 1);
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		PageHelper.startPage(1, 100)
				.doSelectPageInfo(() -> {
					example.createCriteria()
							.andGreaterThan("startDate", date)
							.andLike("businessNo", "33%");
					List list = businessInfoMapper.selectByExample(example);
					System.err.println(list.size());

				});


		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());

	}

	// 测试多表查询，区级别（700w）、市级别（2000w）
	@Test
	public void testMultiTableQuery2() {

		// business_info_330102001, business_info_330102003 (共800万数据)
		StopWatch stopWatch = new StopWatch("区级别查询");
		stopWatch.start();

		// like
		HintManager hintManager = HintManager.getInstance();
		hintManager.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager.addTableShardingValue("business_info", "businessNo", "alltable:sdsd");
		Example example = new Example(BusinessInfo.class);
		LocalDate localDate = LocalDate.of(2018, 4, 1);
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		PageHelper.startPage(1, 100)
				.doSelectPageInfo(() -> {
					example.createCriteria()
							.andGreaterThan("startDate", date)
							.andLike("businessNo", "33%");
					List list = businessInfoMapper.selectByExample(example);
					System.err.println(list.size());

				});

		hintManager.close();
		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());

		// 市级别查询 (共1700万数据)
		// business_info_330102001, business_info_330102003, business_info_330103001, business_info_330103002, business_info_330103003
		StopWatch stopWatch2 = new StopWatch("市级别查询");
		stopWatch2.start();

		// like
		HintManager hintManager2 = HintManager.getInstance();
		hintManager2.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager2.addTableShardingValue("business_info", "businessNo", "alltable");
		Example example2 = new Example(BusinessInfo.class);
		LocalDate localDate2 = LocalDate.of(2018, 4, 1);
		Date date2 = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		PageHelper.startPage(1, 100)
				.doSelectPageInfo(() -> {
					example.createCriteria()
							.andGreaterThan("startDate", date)
							.andLike("businessNo", "33%");
					List list = businessInfoMapper.selectByExample(example);
					System.err.println(list.size());

				});

		hintManager2.close();
		stopWatch2.stop();
		System.err.println(stopWatch2.shortSummary());

	}

	// 测试多表查询，区级别（700w）、市级别（2000w）
	// 不做count
	@Test
	public void testMultiTableQuery2_1() {

		// business_info_330102001, business_info_330102003 (共800万数据)
		StopWatch stopWatch = new StopWatch("区级别查询");
		stopWatch.start();

		// like
		HintManager hintManager = HintManager.getInstance();
		hintManager.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager.addTableShardingValue("business_info", "businessNo", "alltable:sdsd");


		List list = businessInfoMapper.myPageListWithLike(0, 100);
		System.err.println(list.size());

		hintManager.close();
		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());

		// 市级别查询 (共1700万数据)
		// business_info_330102001, business_info_330102003, business_info_330103001, business_info_330103002, business_info_330103003
		StopWatch stopWatch2 = new StopWatch("市级别查询");
		stopWatch2.start();

		// like
		HintManager hintManager2 = HintManager.getInstance();
		hintManager2.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager2.addTableShardingValue("business_info", "businessNo", "alltable");

		List list2 = businessInfoMapper.myPageListWithLike(0, 100);
		System.err.println(list2.size());

		hintManager2.close();
		stopWatch2.stop();
		System.err.println(stopWatch2.shortSummary());

	}


	// 测试多表查询，区级别（700w）
	// 分页跳页
	@Test
	public void testMultiTableQuery3() {

		// business_info_330102001, business_info_330102003 (共800万数据)
		StopWatch stopWatch = new StopWatch("区级别查询");
		stopWatch.start();

		// like
		HintManager hintManager = HintManager.getInstance();
		hintManager.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager.addTableShardingValue("business_info", "businessNo", "alltable:sdsd");
		Example example = new Example(BusinessInfo.class);
		LocalDate localDate = LocalDate.of(2018, 4, 1);
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		PageHelper.startPage(1, 100)
				.doSelectPageInfo(() -> {
					example.createCriteria()
							.andGreaterThan("startDate", date)
							.andLike("businessNo", "33%");

					List list = businessInfoMapper.selectByExample(example);
					System.err.println(list.size());

				});
		stopWatch.stop();
		System.err.println(stopWatch.shortSummary());

		HintManager hintManager2 = HintManager.getInstance();
		hintManager2.addDatabaseShardingValue("business_info", "businessNo", "database");
		hintManager2.addTableShardingValue("business_info", "businessNo", "alltable:sdsd");
		stopWatch.start();
		PageHelper.startPage(10000, 100)
				.doSelectPageInfo(() -> {
					example.createCriteria()
							.andGreaterThan("startDate", date)
							.andLike("businessNo", "33%");
					List list = businessInfoMapper.selectByExample(example);
					System.err.println(list.size());

				});

		System.err.println(stopWatch.shortSummary());


	}
}
