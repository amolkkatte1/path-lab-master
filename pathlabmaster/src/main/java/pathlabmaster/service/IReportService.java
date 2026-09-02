package pathlabmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pathlabmaster.pojo.ReportRegistrationRequest;
import pathlabmaster.utility.Response;

public interface IReportService {

	Response registerReport(ReportRegistrationRequest reportRegistrationRequest) throws JsonMappingException, JsonProcessingException;

}
