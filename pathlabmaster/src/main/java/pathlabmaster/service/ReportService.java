package pathlabmaster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.dao.ParameterMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
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
		Map<String, List<ParameterDetails>> pendingTest = new HashMap<>();
		Map<String, List<ParameterDetails>> completedTest = new HashMap<>();
		reportMaster.setCompletedTest(completedTest);
		List<ParameterDetails> parameterList = new ArrayList<>();
		Map<String, Map<String, Boolean>> reportStatus = new HashMap<>();
		Map<String, Boolean> status = new HashMap<>();
		status.put("isApproved", false);
		status.put("isPrinted", false);
		for(TestMaster test : reportRegistrationRequest.getTestList()) {
			parameterList = new ArrayList<>();
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(Utility.getIds(test.getParameterList()));
			for(ParameterMaster parameter : parameterMasterList) {
				parameterList.add(new ParameterDetails(parameter.getParameterName(), parameter.getValue(),
						parameter.getSequence(), parameter.getDataType(), parameter.getUnit(), parameter.getFormula(),
						parameter.getUpperRange(), parameter.getLowerRange(), parameter.getIsBold()));
			}
			reportStatus.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), status);
			pendingTest.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), parameterList);
		}
		reportMaster.setStatus(reportStatus);
		reportMaster.setPendingTest(pendingTest);
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Report Registration successfully", savedReport);
	}


	@Override
	public Response saveReportDetails(ReportMaster reportMaster) {
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Report Save successfully", savedReport);
	}


	@Override
	public Response addReport(ReportRegistrationRequest reportRegistrationRequest)throws JsonMappingException, JsonProcessingException {
		ReportMaster reportMaster = reportMasterRepo.findByPatientId(reportRegistrationRequest.getPatientId());
		Map<String, List<ParameterDetails>> pendingReportsExisting = reportMaster.getPendingTest();
		List<ParameterDetails> parameterList = new ArrayList<>();
		Map<String, Map<String, Boolean>> reportStatus = reportMaster.getStatus();
		Map<String, Boolean> status = new HashMap<>();
		status.put("isApproved", false);
		status.put("isPrinted", false);
		for(TestMaster test : reportRegistrationRequest.getTestList()) {
			parameterList = new ArrayList<>();
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(Utility.getIds(test.getParameterList()));
			for(ParameterMaster parameter : parameterMasterList) {
				parameterList.add(new ParameterDetails(parameter.getParameterName(), parameter.getValue(),
						parameter.getSequence(), parameter.getDataType(), parameter.getUnit(), parameter.getFormula(),
						parameter.getUpperRange(), parameter.getLowerRange(), parameter.getIsBold()));
			}
			reportStatus.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), status);
			pendingReportsExisting.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), parameterList);
		}
		reportMaster.setStatus(reportStatus);
		reportMaster.setPendingTest(pendingReportsExisting);
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Add Repors successfully", savedReport);
	}


	@Override
	public Response getPendingReportsByPatientIdAndLabId(Long patientId, Long labId) {
		ReportMaster reportMaster = reportMasterRepo.findByPatientIdAndLabId(patientId,labId);
		PatientMaster patientMaster = patientMasterRepo.findByPatientIdAndLabId(patientId,labId);
		ReportMasterResponse reportMasterResponse = new ReportMasterResponse(patientMaster,reportMaster);
		return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponse);
	}


	@Override
	public Response getPendingReportsByLabId(Long labId) {
		String today = Utility.getTodayDate();
		List<ReportMasterResponse> reportMasterResponseList = new ArrayList<>();
		List<ReportMaster> reportMasterList =reportMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		List<PatientMaster> patientMaster = patientMasterRepo.findByLabIdAndCreatedAtStartingWith(labId,today);
		Map<Long, PatientMaster> patientMap = patientMaster.stream().collect(Collectors.toMap(PatientMaster::getPatientId,patient -> patient));
		for(ReportMaster reportMaster:reportMasterList) {
			if (reportMaster.getPendingTest() != null && !reportMaster.getPendingTest().isEmpty() && patientMap.containsKey(reportMaster.getPatientId())) {
				reportMasterResponseList.add(new ReportMasterResponse(patientMap.get(reportMaster.getPatientId()),reportMaster));
			}
		}
		return new Response(ResponseStatus.success, 1, "Get Reports successfully", reportMasterResponseList);
	}


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
	
}
