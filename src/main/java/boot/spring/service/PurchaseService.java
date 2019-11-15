package boot.spring.service;

import java.util.List;
import java.util.Map;

import boot.spring.pagemodel.PurchaseTask;
import org.activiti.engine.runtime.ProcessInstance;

import boot.spring.po.PurchaseApply;

public interface PurchaseService extends ProcessCommon {
    ProcessInstance startWorkflow(PurchaseApply apply, String userid, Map<String, Object> variables);

    PurchaseApply getPurchase(int id);

    void updatePurchase(PurchaseApply a);

    List<PurchaseTask> getPageTasksByGroup(String group, int firstRow, int rowCount);
}
