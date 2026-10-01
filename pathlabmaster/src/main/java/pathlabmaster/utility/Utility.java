package pathlabmaster.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.ParameterDetails;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	
	public static Map<String, Map<String, String>> convertReportDataForDB(
	        Map<String, List<ParameterDetails>> testList) {

	    Map<String, Map<String, String>> updatedTestData = new HashMap<>();

	    if (testList == null || testList.isEmpty()) {
	        return updatedTestData;
	    }

		for (Map.Entry<String, List<ParameterDetails>> entry : testList.entrySet()) {
			String test = entry.getKey();
			System.out.println(test);
			List<ParameterDetails> parameters = entry.getValue();
			Map<String, String> testParameter = new HashMap<>();
			if (parameters != null) {
				for (ParameterDetails parameter : parameters) {
					if (parameter == null) {
						continue;
					}
					String sequence = String.valueOf(parameter.getSequence());
					System.out.println(sequence);
					String value = parameter.getValue() != null ? parameter.getValue() : "";
					System.out.println(value);
					if(value.equals("Titres above 1:80 suggest positive reaction")){
						System.out.println(value);
					}
					String isBold = Boolean.TRUE.equals(parameter.getIsBold()) ? "1" : "0";
					System.out.println(isBold);
					testParameter.put(sequence, value + "|" + isBold);
				}
			}
			updatedTestData.put(test, testParameter);
		}
	    return updatedTestData;
	}
	
	public static Map<String, Map<String, Integer>> convertStatusDataForDB(Map<String, Map<String, Boolean>> status1) {
		Map<String, Map<String, Integer>> statusUpdated = new HashMap<>();
		if (status1 == null || status1.isEmpty()) {
			return statusUpdated;
		}
		for (Map.Entry<String, Map<String, Boolean>> entry : status1.entrySet()) {
			String testName = entry.getKey();
			Map<String, Boolean> statusData = entry.getValue();
			Map<String, Integer> status = new HashMap<>();
			status.put("a", statusData != null && Boolean.TRUE.equals(statusData.get("isApproved")) ? 1 : 0);
			status.put("p", statusData != null && Boolean.TRUE.equals(statusData.get("isPrinted")) ? 1 : 0);
			status.put("s", statusData != null && Boolean.TRUE.equals(statusData.get("isSaved")) ? 1 : 0);
			status.put("i", statusData != null && Boolean.TRUE.equals(statusData.get("isImageUploadEnable")) ? 1 : 0);
			statusUpdated.put(testName, status);
		}
		return statusUpdated;
	}
	
	public static String formatDateTime(String dateTime) {
	    if (dateTime == null || dateTime.trim().isEmpty()) {
	        return "";
	    }

	    try {
	        DateTimeFormatter inputFormatter =
	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        DateTimeFormatter outputFormatter =
	                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

	        LocalDateTime dateTimeValue =
	                LocalDateTime.parse(dateTime.trim(), inputFormatter);

	        return dateTimeValue.format(outputFormatter);

	    } catch (DateTimeParseException e) {
	        return dateTime;
	    }
	}
	public static boolean isWithinLastTwoSeconds(String createdAt) {

	    DateTimeFormatter formatter =
	            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	    LocalDateTime createdDateTime =
	            LocalDateTime.parse(createdAt, formatter);

	    LocalDateTime currentDateTime =
	            LocalDateTime.now();

	    long milliseconds =
	            Duration.between(createdDateTime, currentDateTime).toMillis();

//	    System.out.println("Created At : " + createdDateTime);
//	    System.out.println("Current At : " + currentDateTime);
//	    System.out.println("Difference : " + milliseconds + " ms");

	    return milliseconds >= 0 && milliseconds <= 2000;
	}
}