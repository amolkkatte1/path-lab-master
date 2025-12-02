package pathlabmaster.utility;


public class Response {
	private ResponseStatus status;
	private Integer	statusCode;
	private Integer systemCode;
	private String message;
	private Object data;
	
	public Response() {
		super();
	}
	
	public Response(ResponseStatus status, Integer statusCode, String message) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
	}

	public Response(ResponseStatus status, Integer statusCode, Integer systemCode, String message) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.systemCode = systemCode;
		this.message = message;
	}
	
	public Response(ResponseStatus status, Integer statusCode, String message, Object data) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}
	
	public Response(ResponseStatus status, Integer statusCode, Object data) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.data = data;
	}

	public Response(Integer statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}
	
	public Response(ResponseStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public ResponseStatus getStatus() {
		return status;
	}
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Integer getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(Integer systemCode) {
		this.systemCode = systemCode;
	}

	@Override
	public String toString() {
		return "Response [status=" + status + ", statusCode=" + statusCode + ", systemCode=" + systemCode + ", message="
				+ message + ", data=" + data + "]";
	}
	
}
