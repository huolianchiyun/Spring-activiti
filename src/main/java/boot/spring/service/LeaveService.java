package boot.spring.service;

import java.util.List;
import java.util.Map;

import boot.spring.pagemodel.LeaveTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;

import boot.spring.po.LeaveApply;


public interface LeaveService extends ProcessCommon {
    ProcessInstance startWorkflow(LeaveApply apply, String userId, Map<String, Object> variables);

    LeaveApply getLeave(int id);

    List<LeaveApply> getPageXJTask(String userId, int firstRow, int rowcount);

    int getAllXJTask(String userId);

    List<LeaveApply> getPageUpdateApplyTask(String userId, int firstRow, int rowcount);

    int getAllUpdateApplyTask(String userId);

    void completeReportBack(String taskId, String realStartTime, String realEndTime);

    void updateComplete(String taskId, LeaveApply leave, String reappply);

    List<String> getHighLightedFlows(ProcessDefinitionEntity deployedProcessDefinition, List<HistoricActivityInstance> historicActivityInstances);

    List<LeaveTask> getPageTasksByGroup(String group, int firstRow, int rowCount);
}
