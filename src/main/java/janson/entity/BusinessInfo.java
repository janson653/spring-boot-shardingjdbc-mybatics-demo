package janson.entity;

import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@ToString
@Table(name = "business_info")
public class BusinessInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "businessNo")
	private String businessNo;

	@Column(name = "startDate")
	private Date startDate;

	// 其他字段省略


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
