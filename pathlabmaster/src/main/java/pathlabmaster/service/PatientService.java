package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.LabMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.pojo.LabMaster;
import pathlabmaster.pojo.PatientDashboardResponse;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class PatientService implements IPatientService {

	@Autowired
	private PatientMasterRepository patientRepo;
	@Autowired
	private LabMasterRepository labRepo;

	@Override
	public Response createPatient(PatientMaster patientDetails) {
		Optional<LabMaster> labMaster =  labRepo.findById(patientDetails.getLabId());
		LabMaster lab = labMaster.get();
		patientDetails.setPatientId(Utility.generateId());
		if(lab.getPatientCountAlloted()>=1 && lab.getSbuscriptionEndDate().compareTo(Utility.getTodayDate()) > 0) {
			patientDetails.setUpdatedAt(Utility.getCurrentTime());
			patientDetails.setCreatedAt(Utility.getCurrentTime());
			patientDetails.setAge(patientDetails.getYear());
			PatientMaster savedPatient = patientRepo.save(patientDetails);
			System.out.println(savedPatient.getPatientId());
			lab.setPatientCountAlloted(lab.getPatientCountAlloted()-1);
			labRepo.save(lab);
			return new Response(ResponseStatus.success, 1, "Patient created successfully", savedPatient);
		}else {
			return new Response(ResponseStatus.failure, 0, "Your Subscriptions is Expired Please Contact Admin",null);
		}
	}

	@Override
	public Response updatePatient(PatientMaster patientDetails) {
		patientDetails.setUpdatedAt(Utility.getCurrentTime());
		PatientMaster savedPatient = patientRepo.save(patientDetails);
		System.out.println(savedPatient.getPatientId()); 
		return new Response(ResponseStatus.success, 1, "Patient Update successfully", savedPatient);
	}
	
	@Override
	public Response getPatient(PatientMaster patientDetails) {
		Optional<PatientMaster> optionalPatient = patientRepo.findById(patientDetails.getPatientId());
		PatientMaster Patient = null;
		if (optionalPatient.isPresent()) {
		    Patient = optionalPatient.get();
		} else {
		    throw new RuntimeException("Patient not found");
		}
		return new Response(ResponseStatus.success, 1, "Get Patient successfully", Patient);
	}

	@Override
	public Response getPatientList() {
		List<PatientMaster> PatientList = patientRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Patient List successfully", PatientList);
	}

	@Override
	public Response deletePatient(PatientMaster patientDetails) {
		Optional<PatientMaster> optionalPatient = patientRepo.findById(patientDetails.getPatientId());
		PatientMaster patient = null;
		if (optionalPatient.isPresent()) {
		    patient = optionalPatient.get();
		} else {
		    throw new RuntimeException("Patient not found");
		}
		patientRepo.deleteById(patientDetails.getPatientId());
		return new Response(ResponseStatus.success, 1, "Delete Patient successfully", patient);
	}

	@Override
	public Response getPatientByLabId(Long labId) {
		List<PatientMaster> patientList = patientRepo.findByLabId(labId);

		if (patientList == null || patientList.isEmpty()) {
			return new Response(ResponseStatus.success, 0, "No Patient found for LabId: " + labId,
					patientList);
		}

		return new Response(ResponseStatus.success, 1, "Get Patient List by LabId successfully", patientList);
	}

	@Override
	public Response getTodayPatientCount(Long labId) {
		long patientCount = patientRepo.countByLabIdAndCreatedAtStartingWith(labId, Utility.getTodayDate());
		return new Response(ResponseStatus.success, 1, "Today's patient count fetched successfully", patientCount);
	}

	@Override
	public Response getPatientDashboard(Long labId) {

		// Total patients
		long totalPatients = patientRepo.countByLabId(labId);

		// Current month
		String currentMonth = Utility.getCurrentMonth();

		// Previous month
		String previousMonth = Utility.getPreviousMonthStartDate().substring(0, 7);

		// Current month patient count
		long currentMonthPatients = patientRepo.countByLabIdAndCreatedAtStartingWith(labId, currentMonth);

		// Previous month patient count
		long previousMonthPatients = patientRepo.countByLabIdAndCreatedAtStartingWith(labId, previousMonth);

		// Calculate percentage
		double growthPercentage = 0.0;

		if (previousMonthPatients > 0) {

			growthPercentage = ((double) (currentMonthPatients - previousMonthPatients) / previousMonthPatients) * 100;

			growthPercentage = Math.round(growthPercentage * 10.0) / 10.0;
		}

		PatientDashboardResponse dashboardResponse = new PatientDashboardResponse(totalPatients, currentMonthPatients,
				previousMonthPatients, growthPercentage);

		return new Response(ResponseStatus.success, 1, "Patient dashboard data fetched successfully",
				dashboardResponse);
	}
}
