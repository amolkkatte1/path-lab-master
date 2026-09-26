package pathlabmaster.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.ParameterDetails;

public class Utility {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	// --------------------------------------------------
	// Generate unique ID
	// --------------------------------------------------

	public static Long generateId() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		String idStr = LocalDateTime.now(INDIA_ZONE).format(dtf);

		return Long.parseLong(idStr);
	}

	// --------------------------------------------------
	// Get current date and time
	// Example: 2026-09-15 19:20:30
	// --------------------------------------------------

	public static String getCurrentTime() {

		return LocalDateTime.now(INDIA_ZONE).format(DATE_TIME_FORMATTER);
	}

	// --------------------------------------------------
	// Convert object to JSON
	// --------------------------------------------------

	public static String toJsonString(Object obj) throws JsonProcessingException {

		return objectMapper.writeValueAsString(obj);
	}

	// --------------------------------------------------
	// Convert JSON String to List<Long>
	// --------------------------------------------------

	public static List<Long> getIds(String ids) throws JsonMappingException, JsonProcessingException {

		return objectMapper.readValue(ids, new TypeReference<List<Long>>() {
		});
	}

	// --------------------------------------------------
	// Get today's date
	// Example: 2026-09-15
	// --------------------------------------------------

	public static String getTodayDate() {

		return LocalDate.now(INDIA_ZONE).format(DATE_FORMATTER);
	}

	// --------------------------------------------------
	// Get current month
	// Example: 2026-09
	// --------------------------------------------------

	public static String getCurrentMonth() {

		return LocalDate.now(INDIA_ZONE).format(DateTimeFormatter.ofPattern("yyyy-MM"));
	}

	// --------------------------------------------------
	// Get current month start date
	// Example: 2026-09-01
	// --------------------------------------------------

	public static String getCurrentMonthStartDate() {

		return LocalDate.now(INDIA_ZONE).withDayOfMonth(1).format(DATE_FORMATTER);
	}

	// --------------------------------------------------
	// Get previous month start date
	// Example:
	// Current = 2026-09-15
	// Previous = 2026-08-01
	// --------------------------------------------------

	public static String getPreviousMonthStartDate() {

		return LocalDate.now(INDIA_ZONE).minusMonths(1).withDayOfMonth(1).format(DATE_FORMATTER);
	}

	// --------------------------------------------------
	// Get previous month end date
	// Example:
	// Current = September 2026
	// Result = 2026-08-31
	// --------------------------------------------------

	public static String getPreviousMonthEndDate() {

		return LocalDate.now(INDIA_ZONE).minusMonths(1).withDayOfMonth(1).minusDays(1).format(DATE_FORMATTER);
	}

	// --------------------------------------------------
	// Get current month end date
	// Example: 2026-09-30
	// --------------------------------------------------

	public static String getCurrentMonthEndDate() {

		return LocalDate.now(INDIA_ZONE).withDayOfMonth(1).plusMonths(1).minusDays(1).format(DATE_FORMATTER);
	}

	// --------------------------------------------------
	// Get current year
	// Example: 2026
	// --------------------------------------------------

	public static String getCurrentYear() {

		return String.valueOf(LocalDate.now(INDIA_ZONE).getYear());
	}

	// --------------------------------------------------
	// Get current month number
	// Example: 9
	// --------------------------------------------------

	public static int getCurrentMonthNumber() {

		return LocalDate.now(INDIA_ZONE).getMonthValue();
	}

	// --------------------------------------------------
	// Get previous month
	// Example: 8
	// --------------------------------------------------

	public static int getPreviousMonthNumber() {

		return LocalDate.now(INDIA_ZONE).minusMonths(1).getMonthValue();
	}
	
	public static Map<String, Map<String, String>> convertReportDataForDB(Map<String, List<ParameterDetails>> testList){
		 Map<String, Map<String, String>> updatedTestData = new HashMap<>();
		 Map<String, String> testParameter = new HashMap<>();
		 for(String test : testList.keySet()) {
			 testParameter = new HashMap<>();
			 for(ParameterDetails parameter: testList.get(test)) {
				 testParameter.put(String.valueOf(parameter.getSequence()),  parameter.getValue()+"|"+ parameter.getIsBold() != null&&parameter.getIsBold()?"1":"0");
			 }
			 updatedTestData.put(test, testParameter);
		 }
		return updatedTestData;
	}
}