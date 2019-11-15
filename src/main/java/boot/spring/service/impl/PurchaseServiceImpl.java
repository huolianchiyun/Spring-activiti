package boot.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import boot.spring.pagemodel.PurchaseTask;
import boot.spring.po.LeaveApply;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import boot.spring.mapper.PurchaseApplyMapper;
import boot.spring.po.PurchaseApply;
import boot.spring.service.PurchaseService;

import static boot.spring.common.SpringIOCUtil.getBean;

@Transactional(propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT,timeout=5)
@Service
public class PurchaseServiceImpl implements PurchaseService{
	@Autowired
	PurchaseApplyMapper purchasemapper;
	@Autowired
	IdentityService identityservice;
	@Autowired
	RuntimeService runtimeservice;
	@Autowired
	TaskService taskservice;
	
	public ProcessInstance startWorkflow(PurchaseApply apply, String userid,Map<String, Object> variables) {
		purchasemapper.save(apply);
		String businesskey=String.valueOf(apply.getId());//使用leaveapply表的主键作为businesskey,连接业务数据和流程数据
		identityservice.setAuthenticatedUserId(userid);
		ProcessInstance instance=runtimeservice.startProcessInstanceByKey("purchase",businesskey,variables);
//		String instanceid=instance.getId();
		return instance;
	}

	public PurchaseApply getPurchase(int id) {
		return purchasemapper.getPurchaseApply(id);
	}

	public void updatePurchase(PurchaseApply a) {
		purchasemapper.updateByPrimaryKeySelective(a);
	}

	@Override
	public PurchaseApply getSelfById(int id) {
		return getPurchase(id);
	}

	@Override
	public List<PurchaseTask> getPageTasksByGroup(String group, int firstRow, int rowCount) {
		List<PurchaseTask> results = new ArrayList<>();
		List<Task> tasks =  getBean(TaskService.class).createTaskQuery().taskCandidateGroup(group).listPage(firstRow, rowCount);
		RuntimeService runtimeService = getBean(RuntimeService.class);
		PurchaseApplyMapper purchaseApplyMapper = getBean(PurchaseApplyMapper.class);
		for (Task task : tasks) {
			String instanceId = task.getProcessInstanceId();
			ProcessInstance ins = runtimeservice.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
			String businessKey = ins.getBusinessKey();
			PurchaseApply a = purchaseApplyMapper.getPurchaseApply(Integer.parseInt(businessKey));
			a.setTask(task);
			results.add(new PurchaseTask(a));
		}
		return results;
	}
}
