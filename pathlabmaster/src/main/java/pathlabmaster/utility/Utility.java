package pathlabmaster.utility;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Utility {
	//for generating unique ids for all records
	public static Long generateId() {
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	    String idStr = LocalDateTime.now().format(dtf);
	    return Long.parseLong(idStr);
	}

}
