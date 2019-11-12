package boot.spring.mapper;

import boot.spring.po.BusinessTripApply;

public interface BusinessTripApplyMapper {
	void save(BusinessTripApply apply);

	BusinessTripApply getBusinessTripApply(int id);
	
	void updateByPrimaryKeySelective(BusinessTripApply apply);
}
