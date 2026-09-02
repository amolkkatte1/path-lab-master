package pathlabmaster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.dao.ParameterMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
import pathlabmaster.pojo.ParameterDetails;
import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.pojo.ReportMaster;
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
		List<ParameterDetails> parameterList = new ArrayList<>();
		for(TestMaster test : reportRegistrationRequest.getTestList()) {
			List<ParameterMaster> parameterMasterList = parameterRepo.findByParameterIdIn(Utility.getIds(test.getParameterList()));
			parameterList = new ArrayList<>();
			for(ParameterMaster parameter : parameterMasterList) {
				parameterList.add(new ParameterDetails(parameter.getParameterName(), parameter.getValue(),
						parameter.getSequence(), parameter.getDataType(), parameter.getUnit(), parameter.getFormula(),
						parameter.getUpperRange(), parameter.getLowerRange(), parameter.getIsBold()));
			}
			pendingTest.put(test.getTestName()+"_"+String.valueOf(test.getTestId()), parameterList);
		}
		reportMaster.setPendingTest(pendingTest);
		ReportMaster savedReport = reportMasterRepo.save(reportMaster);
		System.out.println(savedReport.getReportId()); 
		return new Response(ResponseStatus.success, 1, "Report Registration successfully", savedReport);
	}

	
}
