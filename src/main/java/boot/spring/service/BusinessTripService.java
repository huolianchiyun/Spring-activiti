package boot.spring.service;

import boot.spring.mapper.BusinessTripApplyMapper;
import boot.spring.pagemodel.BusinessTripTask;
import boot.spring.po.BusinessTripApply;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static boot.spring.common.SpringIOCUtil.getBean;

public interface BusinessTripService extends ProcessCommon {
    ProcessInstance startWorkflow(BusinessTripApply apply, String userid, Map<String, Object> variables);

    BusinessTripApply getBusinessTripApply(int id);

    void updateBusinessTrip(BusinessTripApply a);

    @Override
    default List<BusinessTripTask> getPageTasksByGroup(String group, int firstRow, int rowCount) {
        List<BusinessTripTask> results = new ArrayList<>();
        List<Task> tasks = getBean(TaskService.class).createTaskQuery().taskCandidateGroup(group).listPage(firstRow, rowCount);
        RuntimeService runtimeService = getBean(RuntimeService.class);
        BusinessTripApplyMapper businessTripApplyMapper = getBean(BusinessTripApplyMapper.class);
        for (Task task : tasks) {
            String instanceId = task.getProcessInstanceId();
            ProcessInstance ins = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            String businessKey = ins.getBusinessKey();
            BusinessTripApply businessTripApply = businessTripApplyMapper.getBusinessTripApply(Integer.parseInt(businessKey));
            businessTripApply.setTask(task);
            results.add(new BusinessTripTask(businessTripApply));
        }
        return results;
    }
}
