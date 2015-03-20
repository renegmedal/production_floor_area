package application.beacon.industrial.service;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface WorkOrderServiceApi {

	public static final String WORKORDER_SVC_PATH = "/workorder";

	@POST(WORKORDER_SVC_PATH )
	public Observable<WorkOrder> saveWorkOrder(@Body WorkOrder workOrder);

	@GET(WORKORDER_SVC_PATH + "/by_uuid/{uuid}")
	public Observable<Collection<WorkOrder>> getWorkOrdersByUuid(@Path("uuid") String uuid);


}
