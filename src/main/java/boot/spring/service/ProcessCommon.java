package boot.spring.service;

import static boot.spring.common.SpringIOCUtil.getBean;
import boot.spring.common.SpringIOCUtil;
import boot.spring.po.Base;
import boot.spring.service.impl.BusinessTripServiceImpl;
import boot.spring.service.impl.LeaveServiceImpl;
import boot.spring.service.impl.PurchaseServiceImpl;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProcessCommon {
    Map<String, Class> ProcessServiceMap = new HashMap() {{
        put("leave", LeaveServiceImpl.class);
        put("purchase", PurchaseServiceImpl.class);
        put("businessTrip", BusinessTripServiceImpl.class);
    }};

    Base getSelfById(int id);

    <T> List<T> getPageTasksByGroup(String group, int firstRow, int rowCount);

    default int getTotalOfTasksByGroup(String group) {
        return getBean(TaskService.class).createTaskQuery().taskCandidateGroup(group).list().size();
    }

    default List<Base> getPageUpdateApplyTask(String userId, String taskKey, String processDefKey, int firstRow, int rowCount) {
        List<Base> results = new ArrayList<>();
        List<Task> tasks = getBean(TaskService.class).createTaskQuery().processDefinitionKey(processDefKey).taskDefinitionKey(taskKey)
                .taskCandidateOrAssigned(userId).listPage(firstRow, rowCount);
        RuntimeService runtimeService = getBean(RuntimeService.class);
        ProcessCommon processCommon = (ProcessCommon) SpringIOCUtil.getBean(ProcessServiceMap.get(processDefKey));
        for (Task task : tasks) {
            String instanceId = task.getProcessInstanceId();
            ProcessInstance ins = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            String businessKey = ins.getBusinessKey();
            Base base = processCommon.getSelfById(Integer.parseInt(businessKey));
            base.setTask(task);
            results.add(base);
        }
        return results;
    }

     default int getAllUpdateApplyTask(String userId, String taskKey, String processDefKey) {
         return (int) getBean(TaskService.class).createTaskQuery().processDefinitionKey(processDefKey).taskDefinitionKey(taskKey).taskCandidateOrAssigned(userId).count();
    }
}
