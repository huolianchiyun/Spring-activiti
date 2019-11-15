package boot.spring.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.activiti.engine.task.Task;

import java.io.Serializable;

@ApiModel("出差表")
@JsonIgnoreProperties(value = {"task"})
public class BusinessTripApply implements Serializable{
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
	Task task;
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
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	
}
