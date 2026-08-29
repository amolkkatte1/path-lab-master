package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class PatientService implements IPatientService {

	@Autowired
	private PatientMasterRepository patientRepo;

	@Override
	public Response createPatient(PatientMaster patientDetails) {
		patientDetails.setPatientId(Utility.generateId());
		patientDetails.setUpdatedAt(Utility.getCurrentTime());
		patientDetails.setCreatedAt(Utility.getCurrentTime());
		PatientMaster savedPatient = patientRepo.save(patientDetails);
		System.out.println(savedPatient.getPatientId()); 
		return new Response(ResponseStatus.success, 1, "Patient created successfully", savedPatient);
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

}
