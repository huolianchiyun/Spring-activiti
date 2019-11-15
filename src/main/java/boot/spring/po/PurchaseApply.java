package boot.spring.po;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.activiti.engine.task.Task;

public class PurchaseApply {
	int id;
	String itemlist;
	BigDecimal total;
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	Date applytime;
	String applyer;
	Task task;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItemlist() {
		return itemlist;
	}
	public void setItemlist(String itemlist) {
		this.itemlist = itemlist;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Date getApplytime() {
		return applytime;
	}
	public void setApplytime(Date applytime) {
		this.applytime = applytime;
	}
	public String getApplyer() {
		return applyer;
	}
	public void setApplyer(String applyer) {
		this.applyer = applyer;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	
}
