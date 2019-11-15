package boot.spring.service;


import static boot.spring.common.SpringIOCUtil.getBean;
import org.activiti.engine.TaskService;

import java.util.List;

public interface ProcessCommon {

    <T> T getSelfById(int id);
    <T> List<T> getPageTasksByGroup(String group, int firstRow, int rowCount);
    default int getTotalOfTasksByGroup(String group){
        return getBean(TaskService.class).createTaskQuery().taskCandidateGroup(group).list().size();
    };
}
