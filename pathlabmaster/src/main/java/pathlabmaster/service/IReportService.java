package pathlabmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pathlabmaster.dao.PatientFilterRequest;
import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.pojo.ReportRegistrationRequest;
import pathlabmaster.utility.Response;

public interface IReportService {

	Response registerReport(ReportRegistrationRequest reportRegistrationRequest) throws JsonMappingException, JsonProcessingException;

	Response saveReportDetails(ReportMaster reportMaster);

	Response addReport(ReportRegistrationRequest reportRegistrationRequest) throws JsonMappingException, JsonProcessingException;

	Response getPendingReportsByPatientIdAndLabId(Long patientId, Long labId) throws JsonMappingException, JsonProcessingException;

	Response getPendingReportsByLabId(Long labId);

	Response getReportsListByLabId(Long labId);

	Response getReportsByFilter(PatientFilterRequest patientFilterRequest);
	
	ReportMaster convertReportMasterForUI(ReportMaster reportMaster) throws JsonMappingException, JsonProcessingException;

}
