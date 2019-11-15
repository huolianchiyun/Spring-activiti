package boot.spring.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import boot.spring.authority.AuthorityCheck;
import boot.spring.po.*;
import boot.spring.service.BusinessTripService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import boot.spring.pagemodel.DataGrid;
import boot.spring.pagemodel.LeaveTask;
import boot.spring.pagemodel.MSG;
import boot.spring.pagemodel.RunningProcess;
import boot.spring.service.LeaveService;
import boot.spring.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "请假流程接口")
@Controller
public class LeaveController {
	//JpaProcessEngineAutoConfiguration->AbstractProcessEngineAutoConfiguration
	@Autowired
	RepositoryService rep;
	@Autowired
	RuntimeService runservice;
	@Autowired
	FormService formservice;
	@Autowired
	IdentityService identityservice;
	@Autowired
	LeaveService leaveservice;
	@Autowired
	BusinessTripService businessTripService;
	@Autowired
	TaskService taskservice;
	@Autowired
	HistoryService histiryservice;
	@Autowired
	SystemService systemservice;



	@RequestMapping(value = "/leaveapply", method = RequestMethod.GET)
	public String leave() {
		return "activiti/leaveapply";
	}

	@RequestMapping(value = "/startleave", method = RequestMethod.POST)
	@ResponseBody
	public MSG startLeave(LeaveApply apply, HttpSession session) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("applyuserid", userid);
		ProcessInstance ins = leaveservice.startWorkflow(apply, userid, variables);
		System.out.println("流程id" + ins.getId() + "已启动");
		return new MSG("sucess");
	}

	@ApiOperation("获取部门领导审批代办列表")
	@RequestMapping(value = "/leave/depttasklist", produces = {"application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<LeaveTask> getLeaveDeptTaskList(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
		DataGrid<LeaveTask> grid = new DataGrid<LeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setRows(new ArrayList<LeaveTask>());
		// 先做权限检查，对于没有部门领导审批权限的用户,直接返回空
		String userid = (String) session.getAttribute("username");
		if(AuthorityCheck.isAuthority(userid, "部门领导审批")){
			grid.setTotal(leaveservice.getTotalOfTasksByGroup("部门经理"));
			grid.setRows(leaveservice.getPageTasksByGroup("部门经理",  (current - 1) * rowCount, rowCount));
		}else {
			grid.setTotal(0);
			grid.setRows(new ArrayList<>(0));
		}
		return grid;
	}

	@RequestMapping(value = "/leave/hrtasklist", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<LeaveTask> getHRTaskList(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
		DataGrid<LeaveTask> grid = new DataGrid<LeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setRows(new ArrayList<LeaveTask>());
		String userId = (String) session.getAttribute("username");
		if (AuthorityCheck.isAuthority(userId, "人事审批")) {
			grid.setTotal(leaveservice.getTotalOfTasksByGroup("人事"));
			grid.setRows(leaveservice.getPageTasksByGroup("人事", (current - 1) * rowCount, rowCount));
		}else {
			grid.setTotal(0);
			grid.setRows(new ArrayList<>(0));
		}
		return grid;
	}

	@RequestMapping(value = "/xjtasklist", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<LeaveTask> getXJTaskList(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		List<LeaveApply> results = leaveservice.getPageXJTask(userid, firstrow, rowCount);
		int totalsize = leaveservice.getAllXJTask(userid);
		List<LeaveTask> tasks = new ArrayList<LeaveTask>();
		for (LeaveApply apply : results) {
			LeaveTask task = new LeaveTask();
			task.setApply_time(apply.getApply_time());
			task.setUser_id(apply.getUser_id());
			task.setEnd_time(apply.getEnd_time());
			task.setId(apply.getId());
			task.setLeave_type(apply.getLeave_type());
			task.setProcess_instance_id(apply.getProcess_instance_id());
			task.setProcessdefid(apply.getTask().getProcessDefinitionId());
			task.setReason(apply.getReason());
			task.setStart_time(apply.getStart_time());
			task.setTaskcreatetime(apply.getTask().getCreateTime());
			task.setTaskid(apply.getTask().getId());
			task.setTaskname(apply.getTask().getName());
			tasks.add(task);
		}
		DataGrid<LeaveTask> grid = new DataGrid<LeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(totalsize);
		grid.setRows(tasks);
		return grid;
	}

	@RequestMapping(value = "/updatetasklist", produces = {
			"application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<LeaveTask> getUpdateTaskList(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		List<LeaveApply> results = leaveservice.getPageUpdateApplyTask(userid, firstrow, rowCount);
		int totalsize = leaveservice.getAllUpdateApplyTask(userid);
		List<LeaveTask> tasks = new ArrayList<LeaveTask>();
		for (LeaveApply apply : results) {
			LeaveTask task = new LeaveTask();
			task.setApply_time(apply.getApply_time());
			task.setUser_id(apply.getUser_id());
			task.setEnd_time(apply.getEnd_time());
			task.setId(apply.getId());
			task.setLeave_type(apply.getLeave_type());
			task.setProcess_instance_id(apply.getProcess_instance_id());
			task.setProcessdefid(apply.getTask().getProcessDefinitionId());
			task.setReason(apply.getReason());
			task.setStart_time(apply.getStart_time());
			task.setTaskcreatetime(apply.getTask().getCreateTime());
			task.setTaskid(apply.getTask().getId());
			task.setTaskname(apply.getTask().getName());
			tasks.add(task);
		}
		DataGrid<LeaveTask> grid = new DataGrid<LeaveTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(totalsize);
		grid.setRows(tasks);
		return grid;
	}

	@RequestMapping(value = "/task/reportcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG reportBackComplete(@PathVariable("taskid") String taskid, HttpServletRequest req) {
		String realstart_time = req.getParameter("realstart_time");
		String realend_time = req.getParameter("realend_time");
		leaveservice.completeReportBack(taskid, realstart_time, realend_time);
		return new MSG("success");
	}

	@RequestMapping(value = "/task/updatecomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG updateComplete(@PathVariable("taskid") String taskid, @ModelAttribute("leave") LeaveApply leave,
			@RequestParam("reapply") String reapply) {
		leaveservice.updateComplete(taskid, leave, reapply);
		return new MSG("success");
	}

	@RequestMapping(value = "myleaveprocess", method = RequestMethod.GET)
	String myleaveprocess() {
		return "activiti/myleaveprocess";
	}

	@RequestMapping(value = "traceprocess/{executionid}", method = RequestMethod.GET)
	public void traceProcess(@PathVariable("executionid") String executionid, HttpServletResponse response)
			throws Exception {
		ProcessInstance process = runservice.createProcessInstanceQuery().processInstanceId(executionid).singleResult();
		BpmnModel bpmnmodel = rep.getBpmnModel(process.getProcessDefinitionId());
		List<String> activeActivityIds = runservice.getActiveActivityIds(executionid);
		DefaultProcessDiagramGenerator gen = new DefaultProcessDiagramGenerator();
		// 获得历史活动记录实体（通过启动时间正序排序，不然有的线可以绘制不出来）
		List<HistoricActivityInstance> historicActivityInstances = histiryservice.createHistoricActivityInstanceQuery()
				.executionId(executionid).orderByHistoricActivityInstanceStartTime().asc().list();
		// 计算活动线
		List<String> highLightedFlows = leaveservice
				.getHighLightedFlows(
						(ProcessDefinitionEntity) ((RepositoryServiceImpl) rep)
								.getDeployedProcessDefinition(process.getProcessDefinitionId()),
						historicActivityInstances);

		InputStream in = gen.generateDiagram(bpmnmodel, "png", activeActivityIds, highLightedFlows, "宋体", "宋体", null,
				null, 1.0);
		// InputStream in=gen.generateDiagram(bpmnmodel, "png",
		// activeActivityIds);
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(in, output);
	}

	@RequestMapping(value = "myleaves", method = RequestMethod.GET)
	String myLeaves() {
		return "activiti/myleaves";
	}

	@RequestMapping(value = "setupprocess", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<RunningProcess> setupProcess(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		System.out.print(userid);
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey("leave").involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			LeaveApply l = leaveservice.getLeave(Integer.parseInt(p.getBusinessKey()));
			if (l.getUser_id().equals(userid))
				list.add(process);
			else
				continue;
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

	@RequestMapping(value = "/leaveHistoryProcess", method = RequestMethod.GET)
	public String history() {
		return "activiti/leaveHistoryProcess";
	}

	@RequestMapping(value = "/leaveDeptLeaderAudit", method = RequestMethod.GET)
	public String myTask() {
		return "activiti/leaveDeptLeaderAudit";
	}

	@RequestMapping(value = "/leaveHRAudit", method = RequestMethod.GET)
	public String hr() {
		return "activiti/leaveHRAudit";
	}


	@RequestMapping(value = "/reportback", method = RequestMethod.GET)
	public String reportBack() {
		return "activiti/reportback";
	}

	@RequestMapping(value = "/modifyapply", method = RequestMethod.GET)
	public String modifyApply() {
		return "modifyLeaveApply";
	}
}
