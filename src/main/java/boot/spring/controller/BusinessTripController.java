package boot.spring.controller;

import boot.spring.authority.AuthorityCheck;
import boot.spring.pagemodel.*;
import boot.spring.po.*;
import boot.spring.service.BusinessTripService;
import boot.spring.service.SystemService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "myBusinessTrips", method = RequestMethod.GET)
    String myBusinessTrips() {
        return "activiti/myBusinessTrips";
    }

    @RequestMapping(value = "myBusinessTripsProcess", method = RequestMethod.GET)
    String myBusinessTripProcess() {
        return "activiti/myBusinessTripsProcess";
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

    @ApiOperation("获取部门领导审批代办列表")
    @RequestMapping(value = "/businessTrip/depttasklist", produces = {"application/json;charset=UTF-8" }, method = RequestMethod.POST)
    @ResponseBody
    public DataGrid<BusinessTripTask> getDeptTaskList(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
        DataGrid<BusinessTripTask> grid = new DataGrid<>();
        grid.setRowCount(rowCount);
        grid.setCurrent(current);
        // 先做权限检查，对于没有部门领导审批权限的用户,直接返回空
        String userId = (String) session.getAttribute("username");
        if(AuthorityCheck.isAuthority(userId, "部门领导审批")){
            grid.setRows(businessTripService.getPageTasksByGroup("部门经理", (current - 1) * rowCount, rowCount));
            grid.setTotal(businessTripService.getTotalOfTasksByGroup("部门经理"));
        }else {
            grid.setTotal(0);
            grid.setRows(new ArrayList<>(0));
        }
        return grid;
    }

    @RequestMapping(value = "/businessTrip/hrtasklist", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public DataGrid<BusinessTripTask> gethrtasklist(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
        DataGrid<BusinessTripTask> grid = new DataGrid<>();
        grid.setRowCount(rowCount);
        grid.setCurrent(current);
        String userId = (String) session.getAttribute("username");
        if (AuthorityCheck.isAuthority(userId, "人事审批")) {
            grid.setTotal(businessTripService.getTotalOfTasksByGroup("人事"));
            grid.setRows( businessTripService.getPageTasksByGroup("人事", (current - 1) * rowCount, rowCount));
        }else {
            grid.setTotal(0);
            grid.setRows(new ArrayList<BusinessTripTask>(0));
        }
        return grid;
    }

    @RequestMapping(value = "/businessTripHistoryProcess", method = RequestMethod.GET)
    public String history() {
        return "activiti/businessTripHistoryProcess";
    }

    @RequestMapping(value = "/businessTripDeptLeaderAudit", method = RequestMethod.GET)
    public String mytask() {
        return "activiti/businessTripDeptLeaderAudit";
    }

    @RequestMapping(value = "/businessTripHRAudit", method = RequestMethod.GET)
    public String hr() {
        return "activiti/businessTripHRAudit";
    }

    @RequestMapping(value = "/modifyBusinessTripApply", method = RequestMethod.GET)
    public String modifyBusinessTripApply() {
        return "activiti/modifyBusinessTripApply";
    }
    @RequestMapping(value = "/processBusinessTripFallback", method = RequestMethod.POST)
    @ResponseBody
    public String processFallback() {
        return "activiti/businessTripHRAudit";
    }
}
