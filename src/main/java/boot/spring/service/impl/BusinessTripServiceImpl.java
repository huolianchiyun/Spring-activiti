package boot.spring.service.impl;

import static boot.spring.common.DateUtils.getCurrentDate;
import boot.spring.mapper.BusinessTripApplyMapper;
import boot.spring.mapper.PurchaseApplyMapper;
import boot.spring.po.BusinessTripApply;
import boot.spring.po.LeaveApply;
import boot.spring.po.PurchaseApply;
import boot.spring.service.BusinessTripService;
import boot.spring.service.PurchaseService;
import com.google.common.collect.Maps;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 50)
@Service
public class BusinessTripServiceImpl implements BusinessTripService {
    @Autowired
    BusinessTripApplyMapper businessTripApplyMapper;
    @Autowired
    IdentityService identityservice;
    @Autowired
    RuntimeService runtimeservice;
    @Autowired
    TaskService taskservice;

    public ProcessInstance startWorkflow(BusinessTripApply apply, String userid, Map<String, Object> variables) {
        apply.setApply_time(getCurrentDate());
        apply.setUser_id(userid);
        businessTripApplyMapper.save(apply);
        String businesskey = String.valueOf(apply.getId());//使用businesstripapply表的主键作为businesskey,连接业务数据和流程数据
        identityservice.setAuthenticatedUserId(userid);
        ProcessInstance instance = runtimeservice.startProcessInstanceByKey("businessTrip", businesskey, variables);
        Task task = taskservice.createTaskQuery().executionId(instance.getId()).singleResult();
        taskservice.claim(task.getId(), userid);
        Map<String, Object> vars = Maps.newHashMap();
        vars.put("submit", true);
        taskservice.complete(task.getId(), vars);
        apply.setProcess_instance_id(instance.getId());
        businessTripApplyMapper.updateByPrimaryKeySelective(apply);
        return instance;
    }


    public BusinessTripApply getBusinessTripApply(int id) {
        return businessTripApplyMapper.getBusinessTripApply(id);
    }

    public void updateBusinessTrip(BusinessTripApply a) {
        businessTripApplyMapper.updateByPrimaryKeySelective(a);
    }

    @Override
    public BusinessTripApply getSelfById(int id) {
        return getBusinessTripApply(id);
    }
}
