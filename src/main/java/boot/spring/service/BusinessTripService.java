package boot.spring.service;

import boot.spring.po.BusinessTripApply;
import boot.spring.po.PurchaseApply;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.Map;

public interface BusinessTripService {
    ProcessInstance startWorkflow(BusinessTripApply apply, String userid, Map<String, Object> variables);

    BusinessTripApply getBusinessTripApply(int id);

    void updateBusinessTrip(BusinessTripApply a);
}
