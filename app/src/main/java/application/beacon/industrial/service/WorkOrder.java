package application.beacon.industrial.service;


public class WorkOrder {

	private Long id;
	private String uuid;
	private String orderId;
	private String description;
 	private String assignTo;
 	private String status;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nBeacon[")
		  .append(" id=" + id)
		  .append(" uuid=" + uuid)
		  .append(" orderId=" + orderId)
		  .append(" description=" + description)
		  .append(" assignTo=" + assignTo)
		  .append(" status=" + status)
		  .append("]");
		return sb.toString();
	}
}
