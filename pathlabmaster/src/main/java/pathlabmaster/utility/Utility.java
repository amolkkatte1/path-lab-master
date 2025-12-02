package pathlabmaster.utility;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Utility {
	//for generating unique ids for all records
	public static Long generateId() {
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	    String idStr = LocalDateTime.now().format(dtf);
	    return Long.parseLong(idStr);
	}
	
	public static String getCurrentTime() {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    return LocalDateTime.now(ZoneId.of("Asia/Kolkata")).format(formatter);
	}
	
	public static String toJsonString(Object obj) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
