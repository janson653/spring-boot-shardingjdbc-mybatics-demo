package janson.mapper;

import janson.entity.BusinessInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface BusinessInfoMapper extends Mapper<BusinessInfo>, IdsMapper<BusinessInfo>, InsertListMapper<BusinessInfo> {


	public List<BusinessInfo> myPageListWithLike(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
}