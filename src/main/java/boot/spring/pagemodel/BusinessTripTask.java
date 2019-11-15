package boot.spring.pagemodel;

import boot.spring.po.BusinessTripApply;
import boot.spring.po.LeaveApply;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel("出差任务信息")
public class BusinessTripTask {
    public BusinessTripTask() {
    }

    public BusinessTripTask(BusinessTripApply businessTripApply) {
        id = businessTripApply.getId();
        apply_time = businessTripApply.getApply_time();
        user_id = businessTripApply.getUser_id();
        end_time = businessTripApply.getEnd_time();
        process_instance_id = businessTripApply.getProcess_instance_id();
        processdefid = businessTripApply.getTask().getProcessDefinitionId();
        reason = businessTripApply.getReason();
        start_time = businessTripApply.getStart_time();
        taskcreatetime = businessTripApply.getTask().getCreateTime();
        taskid = businessTripApply.getTask().getId();
        taskname = businessTripApply.getTask().getName();
    }

    @ApiModelProperty("主键")
    int id;
    @ApiModelProperty("流程实例id")
    String process_instance_id;
    @ApiModelProperty("用户名")
    String user_id;
    @ApiModelProperty("出差起始时间")
    String start_time;
    @ApiModelProperty("出差结束时间")
    String end_time;
    @ApiModelProperty("出差原因")
    String reason;
    @ApiModelProperty("申请时间")
    String apply_time;
    @ApiModelProperty("实际出差起始时间")
    String reality_start_time;
    @ApiModelProperty("实际出差结束时间")
    String reality_end_time;
    @ApiModelProperty("任务id")
    String taskid;
    @ApiModelProperty("任务名")
    String taskname;
    @ApiModelProperty("流程实例id")
    String processinstanceid;
    @ApiModelProperty("流程定义id")
    String processdefid;
    @ApiModelProperty("任务创建时间")
//    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    Date taskcreatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcess_instance_id() {
        return process_instance_id;
    }

    public void setProcess_instance_id(String process_instance_id) {
        this.process_instance_id = process_instance_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getReality_start_time() {
        return reality_start_time;
    }

    public void setReality_start_time(String reality_start_time) {
        this.reality_start_time = reality_start_time;
    }

    public String getReality_end_time() {
        return reality_end_time;
    }

    public void setReality_end_time(String reality_end_time) {
        this.reality_end_time = reality_end_time;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getProcessinstanceid() {
        return processinstanceid;
    }

    public void setProcessinstanceid(String processinstanceid) {
        this.processinstanceid = processinstanceid;
    }

    public String getProcessdefid() {
        return processdefid;
    }

    public void setProcessdefid(String processdefid) {
        this.processdefid = processdefid;
    }

    public Date getTaskcreatetime() {
        return taskcreatetime;
    }

    public void setTaskcreatetime(Date taskcreatetime) {
        this.taskcreatetime = taskcreatetime;
    }

}
