package boot.spring.controller;

import boot.spring.common.SpringIOCUtil;
import boot.spring.pagemodel.Process;
import boot.spring.pagemodel.*;
import boot.spring.service.BusinessTripService;
import boot.spring.service.ProcessCommon;
import boot.spring.service.SystemService;
import boot.spring.service.impl.BusinessTripServiceImpl;
import boot.spring.service.impl.LeaveServiceImpl;
import boot.spring.service.impl.PurchaseServiceImpl;
import io.swagger.annotations.Api;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "请假流程接口")
@Controller
public class ProcessCommonController {
	private final static Map<String,Class> servicesMap = new HashMap(){{
		put("leave", LeaveServiceImpl.class);
		put("purchase", PurchaseServiceImpl.class);
		put("businessTrip", BusinessTripServiceImpl.class);
	}};

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
	BusinessTripService businessTripService;
	@Autowired
	TaskService taskservice;
	@Autowired
	HistoryService histiryservice;
	@Autowired
	SystemService systemservice;

	@RequestMapping(value = "/processlist", method = RequestMethod.GET)
	String process() {
		return "activiti/processlist";
	}

	@RequestMapping(value = "/uploadworkflow", method = RequestMethod.POST)
	public String fileupload(@RequestParam MultipartFile uploadfile, HttpServletRequest request) {
		try {
			MultipartFile file = uploadfile;
			String filename = file.getOriginalFilename();
			InputStream is = file.getInputStream();
			rep.createDeployment().addInputStream(filename, is).deploy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}

	@RequestMapping(value = "/getprocesslists", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Process> getlist(@RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		List<ProcessDefinition> list = rep.createProcessDefinitionQuery().listPage(firstrow, rowCount);
		int total = rep.createProcessDefinitionQuery().list().size();
		List<Process> mylist = new ArrayList<Process>();
		for (int i = 0; i < list.size(); i++) {
			Process p = new Process();
			p.setDeploymentId(list.get(i).getDeploymentId());
			p.setId(list.get(i).getId());
			p.setKey(list.get(i).getKey());
			p.setName(list.get(i).getName());
			p.setResourceName(list.get(i).getResourceName());
			p.setDiagramresourcename(list.get(i).getDiagramResourceName());
			mylist.add(p);
		}
		DataGrid<Process> grid = new DataGrid<Process>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(mylist);
		grid.setTotal(total);
		return grid;
	}

	@RequestMapping(value = "/showresource", method = RequestMethod.GET)
	public void export(@RequestParam("pdid") String pdid, @RequestParam("resource") String resource,
			HttpServletResponse response) throws Exception {
		ProcessDefinition def = rep.createProcessDefinitionQuery().processDefinitionId(pdid).singleResult();
		InputStream is = rep.getResourceAsStream(def.getDeploymentId(), resource);
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(is, output);
	}

	@RequestMapping(value = "/deletedeploy", method = RequestMethod.POST)
	public String deletedeploy(@RequestParam("deployid") String deployid) throws Exception {
		rep.deleteDeployment(deployid, true);
		return "activiti/processlist";
	}

	@RequestMapping(value = "/runningprocess", method = RequestMethod.GET)
	public String task() {
		return "activiti/runningprocess";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String my() {
		return "index";
	}

	@RequestMapping(value = "/activiti/task-deptleaderaudit", method = RequestMethod.GET)
	String url() {
		return "/activiti/task-deptleaderaudit";
	}

	@RequestMapping(value = "/task/deptcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG deptcomplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("deptleaderapprove");
		variables.put("deptleaderapprove", approve);
		taskservice.claim(taskid, userid);
		taskservice.complete(taskid, variables);
		return new MSG("success");
	}

	@RequestMapping(value = "/task/hrcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG hrcomplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("hrapprove");
		variables.put("hrapprove", approve);
		taskservice.claim(taskid, userid);
		taskservice.complete(taskid, variables);
		return new MSG("success");
	}


	@RequestMapping(value = "/processinfo", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricActivityInstance> processinfo(@RequestParam("instanceid") String instanceid) {
		List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery()
				.processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
		return his;
	}

	@RequestMapping(value = "/processhis", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricActivityInstance> processhis(@RequestParam("ywh") String ywh) {
		String instanceid = histiryservice.createHistoricProcessInstanceQuery().processDefinitionKey("purchase")
				.processInstanceBusinessKey(ywh).singleResult().getId();
		List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery()
				.processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
		return his;
	}

	@RequestMapping(value = "allRunningProcessesRelatedToMe", method = RequestMethod.POST) // 参与的正在运行的流程
	@ResponseBody
	public DataGrid<RunningProcess> getAllRunningProcessesRelatedToMe(HttpSession session, @RequestParam("type") String type,
				 @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey(type).involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			list.add(process);
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

	@RequestMapping(value = "/getfinishprocess", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<HistoryProcess> getHistory(HttpSession session, @RequestParam("type")String type, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
		String userid = (String) session.getAttribute("username");
		HistoricProcessInstanceQuery process = histiryservice.createHistoricProcessInstanceQuery()
				.processDefinitionKey(type).startedBy(userid).finished();
		int total = (int) process.count();
		int firstrow = (current - 1) * rowCount;
		List<HistoricProcessInstance> info = process.listPage(firstrow, rowCount);
		List<HistoryProcess> list = new ArrayList<HistoryProcess>();
		dealHistoryProcessByType(info, list, type);
		DataGrid<HistoryProcess> grid = new DataGrid<HistoryProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

	@RequestMapping(value = "/dealtask", method = RequestMethod.POST)
	@ResponseBody
	public Object taskdeal(@RequestParam("taskid") String taskid, @RequestParam("type") String type, HttpServletResponse response) {
		Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
		ProcessInstance process = runservice.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		ProcessCommon processCommon = (ProcessCommon) SpringIOCUtil.getBean(servicesMap.get(type));
		return processCommon.getSelfById(new Integer(process.getBusinessKey()));
	}

	private void dealHistoryProcessByType(List<HistoricProcessInstance> info, List<HistoryProcess> list, String type) {
		ProcessCommon processCommon = (ProcessCommon) SpringIOCUtil.getBean(servicesMap.get(type));
		for (HistoricProcessInstance history : info) {
			HistoryProcess his = new HistoryProcess();
			String bussinesskey = history.getBusinessKey();
			his.setBusinessKey(bussinesskey);
			his.setProcessDefinitionId(history.getProcessDefinitionId());
			Object apply = processCommon.getSelfById(Integer.parseInt(bussinesskey));
			his.setData(apply);
			list.add(his);
		}
	}


}
