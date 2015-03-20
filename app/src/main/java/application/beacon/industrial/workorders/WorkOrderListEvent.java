package application.beacon.industrial.workorders;

import java.util.Collection;

import application.beacon.industrial.service.WorkOrder;

/**
 * Created by rene on 2/13/15.
 */
public class WorkOrderListEvent {

    private Collection<WorkOrder> workOrderList;

    public Collection<WorkOrder> getWorkOrderList() {
        return workOrderList;
    }

    public void setWorkOrderList(Collection<WorkOrder> workOrderList) {
        this.workOrderList = workOrderList;
    }
}
