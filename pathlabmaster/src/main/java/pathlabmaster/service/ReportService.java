package pathlabmaster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pathlabmaster.dao.ParameterMasterRepository;
import pathlabmaster.dao.PatientFilterRequest;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
import pathlabmaster.dao.TestMasterRepository;
import pathlabmaster.pojo.ParameterDetails;
import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.pojo.ReportMasterResponse;
import pathlabmaster.pojo.ReportRegistrationRequest;
import pathlabmaster.pojo.TestMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class ReportService implements IReportService {

	@Autowired
	private ReportMasterRepository reportMasterRepo;
	@Autowired
	private ParameterMasterRepository parameterRepo;
	@Autowired
	private PatientMasterRepository patientMasterRepo;
	@Autowired
	private TestMasterRepository testRepo;
	

	@Override
	public Response registerReport(ReportRegistrationRequest reportRegistrationRequest) throws JsonMappingException, JsonProcessingException {
		ReportMaster reportMaster = new ReportMaster();
		reportMaster.setReportId(Utility.generateId());
		reportMaster.setUpdatedAt(Utility.getCurrentTime());
		reportMaster.setCreatedAt(Utility.getCurrentTime());
		reportMaster.setCreatedBy(reportRegistrationRequest.getUserId());
		reportMaster.setUpdatedBy(reportRegistrationRequest.getUserId());
		reportMaster.setLabId(reportRegistrationRequest.getLabId());
		reportMaster.setPatientId(reportRegistrationRequest.getPatientId());
		Map<String, Map<String, String>> pendingTest1 = new HashMap<>();
		Map<String, Map<String, String>> completedTest1 = new HashMap<>();
		reportMaster.setCompletedTest1(completedTest1);
		Map<String, String>parameterList1= new HashMap<>();
		Map<String, Map<String, Integer>> reportStatus1 = new HashMap<>();
		Map<String, Integer> status1 = new HashMap<>();
		status1.put("a", 0);
		status1.put("p", 0);
		status1.put("s", 0);
		status1.put("i", 0);
		Map<String, Integer> reportNames = new HashMap<>();
		for (TestMaster test : reportRegistrationRequest.getTestList()) {
			status1.put("i", Boolean.TRUE.equals(test.getIsImageUploadEnable())?1:0);
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(Utility.getIds(test.getParameterList()));
			for(ParameterMaster parameter : parameterMasterList) {
				 parameterList1.put(String.valueOf(parameter.getSequence()),parameter.getValue() + "|" +(parameter.getIsBold() != null && parameter.getIsBold() ? "1" : "0"));
			}
			reportStatus1.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), status1);
			pendingTest1.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), parameterList1);
			reportNames.put(test.getTestName(),test.getTestCharges());
		}
		reportMaster.setReportNameList(reportNames);
		reportMaster.setStatus1(reportStatus1);
		reportMaster.setPendingTest1(pendingTest1);
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Report Registration successfully", savedReport);
	}


	@Override
	public Response saveReportDetails(ReportMaster reportMaster) {
		ReportMaster reportMasterExisting = reportMasterRepo.findByPatientIdAndLabId(reportMaster.getPatientId(),reportMaster.getLabId());
		reportMasterExisting.setPendingTest1(Utility.convertReportDataForDB(reportMaster.getPendingTest()));
		reportMasterExisting.setCompletedTest1(Utility.convertReportDataForDB(reportMaster.getCompletedTest()));
		reportMasterExisting.setStatus1(convertStatusDataForDB(reportMaster.getStatus()));
		reportMasterExisting.setUpdatedAt(Utility.getCurrentTime());
		reportMasterExisting.setUpdatedBy(reportMaster.getUpdatedBy());
		ReportMaster savedReport = reportMasterRepo.save(reportMasterExisting);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Report Save successfully", savedReport);
	}

	

	private Map<String, Map<String, Integer>> convertStatusDataForDB(Map<String, Map<String, Boolean>> status1) {
		Map<String, Map<String, Integer>> statusUpdated = new HashMap<>();
		for(String testName: status1.keySet()) {
			Map<String, Integer> status = new HashMap<>();
			status.put("a", status1.get(testName).get("isApproved")?1:0);
			status.put("p", status1.get(testName).get("isPrinted")?1:0);
			status.put("s", status1.get(testName).get("isSaved")?1:0);
			status.put("i", status1.get(testName).get("isImageUploadEnable")?1:0);
			statusUpdated.put(testName, status);
		}
		return statusUpdated;
	}


	@Override
	public Response addReport(ReportRegistrationRequest reportRegistrationRequest)throws JsonMappingException, JsonProcessingException {
		ReportMaster reportMaster = reportMasterRepo.findByPatientId(reportRegistrationRequest.getPatientId());
		Map<String, Map<String, String>> pendingReportsExisting1 = reportMaster.getPendingTest1();
		Map<String, Integer> status1 = new HashMap<>();
		Map<String, String>parameterList1= new HashMap<>();
		Map<String, Map<String, Integer>> reportStatus1 = reportMaster.getStatus1();
		status1.put("a", 0);
		status1.put("p", 0);
		status1.put("s", 0);
		Map<String,Integer> reportNames = new HashMap<>();
		for(TestMaster test : reportRegistrationRequest.getTestList()) {
			status1.put("i", Boolean.TRUE.equals(test.getIsImageUploadEnable())?1:0);
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(Utility.getIds(test.getParameterList()));
			for(ParameterMaster parameter : parameterMasterList) {
				 parameterList1.put(String.valueOf(parameter.getSequence()),parameter.getValue() + "|" +(parameter.getIsBold() != null && parameter.getIsBold() ? "1" : "0"));
			}
			reportStatus1.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), status1);
			pendingReportsExisting1.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), parameterList1);
			reportNames.put(test.getTestName(),test.getTestCharges());
		}
		reportMaster.setReportNameList(reportNames);
		reportMaster.setStatus1(reportStatus1);
		reportMaster.setPendingTest1(pendingReportsExisting1);
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Add Repors successfully", savedReport);
	}


	@Override
	public Response getPendingReportsByPatientIdAndLabId(Long patientId, Long labId) throws JsonMappingException, JsonProcessingException {
		ReportMaster reportMaster = convertReportMasterForUI(reportMasterRepo.findByPatientIdAndLabId(patientId, labId));
		PatientMaster patientMaster = patientMasterRepo.findByPatientIdAndLabId(patientId, labId);
		if (patientMaster != null && reportMaster != null) {
			ReportMasterResponse reportMasterResponse = new ReportMasterResponse(patientMaster, reportMaster);
			return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponse);
		} else {
			return new Response(ResponseStatus.failure, 0, "Reports not yet Register", null);
		}
	}


	@Override
	public Response getPendingReportsByLabId(Long labId) {
		String today = Utility.getTodayDate();
		List<ReportMasterResponse> reportMasterResponseList = new ArrayList<>();
		List<ReportMaster> reportMasterList =reportMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		List<PatientMaster> patientMaster = patientMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		Map<Long, PatientMaster> patientMap = patientMaster.stream().collect(Collectors.toMap(PatientMaster::getPatientId,patient -> patient));
		for(ReportMaster reportMaster:reportMasterList) {
			Map<String, List<ParameterDetails>> tempPendigTest = new HashMap<>();
			List<ParameterDetails> list = new ArrayList<>();
			for(String test: reportMaster.getPendingTest1().keySet()) {
				tempPendigTest.put(test, list);
			}
			reportMaster.setPendingTest(tempPendigTest);
			Map<String, List<ParameterDetails>> tempCompletedTest = new HashMap<>();
			for(String test: reportMaster.getCompletedTest().keySet()) {
				tempCompletedTest.put(test, list);
			}
			reportMaster.setPendingTest(tempCompletedTest);
			if (reportMaster.getPendingTest() != null && !reportMaster.getPendingTest().isEmpty() && patientMap.containsKey(reportMaster.getPatientId())) {
				reportMasterResponseList.add(new ReportMasterResponse(patientMap.get(reportMaster.getPatientId()),reportMaster));
			}
		}
		return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponseList);
	}

	private ReportMaster convertReportMasterForUI(ReportMaster reportMaster) throws JsonMappingException, JsonProcessingException {
		if (reportMaster != null) {
			List<Long> testIds = Stream.concat(
					reportMaster.getPendingTest1() != null ? reportMaster.getPendingTest1().keySet().stream()
							: Stream.empty(),

					reportMaster.getCompletedTest1() != null ? reportMaster.getCompletedTest1().keySet().stream()
							: Stream.empty())
					.map(key -> Long.valueOf(key.substring(key.lastIndexOf("_") + 1))).toList();
			List<TestMaster> testMasterList = testRepo.findByTestIdIn(testIds);
			Map<Long, TestMaster> testMasterMap = testMasterList.stream()
					.collect(Collectors.toMap(TestMaster::getTestId, testMaster -> testMaster));
			List<Long> parameterIds = new ArrayList<>();

			for (TestMaster test : testMasterList) {

				if (test.getParameterList() != null && !test.getParameterList().isBlank()) {

					parameterIds.addAll(Utility.getIds(test.getParameterList()));
				}
			}
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(parameterIds);
			Map<Long, ParameterMaster> parameterMasterMap = parameterMasterList.stream()
					.collect(Collectors.toMap(ParameterMaster::getParameterId, parameter -> parameter));
			if (reportMaster.getCompletedTest1() != null && !reportMaster.getCompletedTest1().isEmpty()) {
				reportMaster
						.setPendingTest(convertReportMasterDataForUI(reportMaster.getCompletedTest1(), testMasterMap,parameterMasterMap));
			}
			if (reportMaster.getCompletedTest1() != null && !reportMaster.getCompletedTest1().isEmpty()) {
				reportMaster.setCompletedTest(
						convertReportMasterDataForUI(reportMaster.getCompletedTest1(), testMasterMap,parameterMasterMap));
			}
			if (reportMaster.getStatus1() != null && !reportMaster.getStatus1().isEmpty()) {
				reportMaster.setStatus(convertStatusDataForUI(reportMaster.getStatus1()));
			}
			
		}
		return reportMaster;
	}

	private Map<String, Map<String, Boolean>> convertStatusDataForUI(Map<String, Map<String, Integer>> status1) {
		 Map<String, Map<String, Boolean>> updatedStatusData = new HashMap<>();
		for(String testName :status1.keySet()) {
			Map<String, Boolean> status = new HashMap<>();
			status.put("isApproved", status1.get(testName).get("a")==1?true:false);
			status.put("isPrinted", status1.get(testName).get("p")==1?true:false);
			status.put("isSaved", status1.get(testName).get("s")==1?true:false);
			status.put("isImageUploadEnable", status1.get(testName).get("i")==1?true:false);
			updatedStatusData.put(testName, status);
		}
		return updatedStatusData;
	}


	private Map<String, List<ParameterDetails>> convertReportMasterDataForUI(Map<String, Map<String, String>> testList, Map<Long, TestMaster> testMasterMap, Map<Long, ParameterMaster> parameterMasterMap) throws JsonMappingException, JsonProcessingException {
		Map<String, List<ParameterDetails>> testDataForUi = new HashMap<>();
		for(String test :testList.keySet()) {
			List<ParameterDetails> parameterList = new ArrayList<>();
			TestMaster testStructureData = testMasterMap.get(test.substring(test.lastIndexOf("_") + 1));
			List<Long> parameterListOriginal = Utility.getIds(testStructureData.getParameterList());
			for(Long parameterId : parameterListOriginal) {
				ParameterMaster parameter = parameterMasterMap.get(parameterId);
				parameterList .add(new ParameterDetails(
			        parameter.getParameterName(),
			        testList.get(test).get(parameter.getSequence()).split("\\|")[0],
			        parameter.getSequence(),
			        parameter.getDataType(),
			        parameter.getUnit(),
			        parameter.getFormula(),
			        parameter.getUpperRange(),
			        parameter.getLowerRange(),
			        testList.get(test).get(parameter.getSequence()).split("\\|")[1].equals(1)?true:false,
			        parameter.getIsNameBold(),
			        parameter.getIsDescriptionParameter(),
			        parameter.getPosition(),
			        parameter.getParameterRange(),
			        parameter.getIsValueRequired(),
			        parameter.getLineCount(),
			        parameter.getIsValueDiscription()
			));
			}
			testDataForUi.put(test, parameterList);
		}
			
		return testDataForUi;
	}


//currently not using
	@Override
	public Response getReportsListByLabId(Long labId) {
		String today = Utility.getTodayDate();
		List<ReportMasterResponse> reportMasterResponseList = new ArrayList<>();
		List<ReportMaster> reportMasterList =reportMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		List<PatientMaster> patientMaster = patientMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		Map<Long, PatientMaster> patientMap = patientMaster.stream().collect(Collectors.toMap(PatientMaster::getPatientId,patient -> patient));
		for(ReportMaster reportMaster:reportMasterList) {
			if(patientMap.containsKey(reportMaster.getPatientId())) {
				reportMasterResponseList.add(new ReportMasterResponse(patientMap.get(reportMaster.getPatientId()),reportMaster));
			}
		}
		return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponseList);
	}


	@Override
	public Response getReportsByFilter(PatientFilterRequest patientFilterRequest) {
		String today = Utility.getTodayDate();
		List<ReportMasterResponse> reportMasterResponseList = new ArrayList<>();
		List<ReportMaster> reportMasterList =reportMasterRepo.filterReports(
		        patientFilterRequest.getLabId(),
		        patientFilterRequest.getFromDate(),
		        patientFilterRequest.getToDate(),
		        patientFilterRequest.getPatientId()
		);
		List<PatientMaster> patientMaster = patientMasterRepo.filterPatients(
		        patientFilterRequest.getLabId(),
		        patientFilterRequest.getFromDate(),
		        patientFilterRequest.getToDate(),
		        patientFilterRequest.getFirstName(),
		        patientFilterRequest.getLastName(),
		        patientFilterRequest.getPatientId(),
		        patientFilterRequest.getDoctorName(),
		        patientFilterRequest.getDoctorId()
		);
//		Map<Long, PatientMaster> patientMap = patientMaster.stream().collect(Collectors.toMap(PatientMaster::getPatientId,patient -> patient));
		Map<Long, ReportMaster> reportMap = reportMasterList.stream().collect(Collectors.toMap(ReportMaster::getPatientId,report -> report));
		for(PatientMaster patient :patientMaster) {
			if(reportMap.containsKey(patient.getPatientId())){
				reportMasterResponseList.add(new ReportMasterResponse(patient,reportMap.get(patient.getPatientId())));
			}
//				else {
//				reportMasterResponseList.add(new ReportMasterResponse(patient));
//			}
		}
		
		return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponseList);
	}
	
}
