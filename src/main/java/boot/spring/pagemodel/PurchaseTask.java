package boot.spring.pagemodel;

import boot.spring.po.PurchaseApply;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.activiti.engine.task.Task;

import java.math.BigDecimal;
import java.util.Date;

public class PurchaseTask {
    public PurchaseTask() {
    }

    public PurchaseTask(PurchaseApply purchaseApply) {
        applyer = purchaseApply.getApplyer();
        applytime = purchaseApply.getApplytime();
        bussinesskey = purchaseApply.getId();
        itemlist = purchaseApply.getItemlist();
		total = purchaseApply.getTotal();
		Task task = purchaseApply.getTask();
		processdefid = task.getProcessDefinitionId();
		processinstanceid = task.getProcessInstanceId();
		taskid = task.getId();
		taskname = task.getName();
    }

    int bussinesskey;
    String applyer;
    String itemlist;
    BigDecimal total;
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    Date applytime;
    String taskid;
    String taskname;
    String processinstanceid;
    String processdefid;

    public int getBussinesskey() {
        return bussinesskey;
    }

    public void setBussinesskey(int bussinesskey) {
        this.bussinesskey = bussinesskey;
    }

    public String getApplyer() {
        return applyer;
    }

    public void setApplyer(String applyer) {
        this.applyer = applyer;
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

}
