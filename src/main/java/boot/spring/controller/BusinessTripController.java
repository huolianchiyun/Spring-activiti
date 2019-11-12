package boot.spring.controller;

import boot.spring.pagemodel.Process;
import boot.spring.pagemodel.*;
import boot.spring.po.*;
import boot.spring.service.BusinessTripService;
import boot.spring.service.LeaveService;
import boot.spring.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
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
public class BusinessTripController {
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
	TaskService taskservice;
	@Autowired
	HistoryService histiryservice;
	@Autowired
	SystemService systemservice;
	@Autowired
	BusinessTripService businessTripService;


	@RequestMapping(value = "/businessTrip", method = RequestMethod.GET)
	public String businessTrip() {
		return "activiti/businessTripApply";
	}

	@RequestMapping(value = "/startBusinessTrip", method = RequestMethod.POST)
	@ResponseBody
	public MSG start_BusinessTrip(BusinessTripApply apply, HttpSession session) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("applyuserid", userid);
		ProcessInstance ins = businessTripService.startWorkflow(apply, userid, variables);
		System.out.println("流程id" + ins.getId() + "已启动");
		return new MSG("sucess");
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


	@RequestMapping(value = "involvedprocess", method = RequestMethod.POST) // 参与的正在运行的请假流程
	@ResponseBody
	public DataGrid<RunningProcess> allexeution(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
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
			list.add(process);
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}


	@RequestMapping(value = "/historyprocess", method = RequestMethod.GET)
	public String history() {
		return "activiti/historyprocess";
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

	@RequestMapping(value = "myleaveprocess", method = RequestMethod.GET)
	String myleaveprocess() {
		return "activiti/myleaveprocess";
	}


	@RequestMapping(value = "myleaves", method = RequestMethod.GET)
	String myleaves() {
		return "activiti/myleaves";
	}

	@RequestMapping(value = "myBusinessTrips", method = RequestMethod.GET)
	String myBusinessTrips() {
		return "activiti/myBusinessTrips";
	}


	@RequestMapping(value = "businessTriptasklist", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<RunningProcess> getBusinessTriptasklist(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey("businessTrip").involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			BusinessTripApply l = businessTripService.getBusinessTripApply(Integer.parseInt(p.getBusinessKey()));
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

}
