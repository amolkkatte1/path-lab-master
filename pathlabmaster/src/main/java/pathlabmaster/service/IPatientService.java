package pathlabmaster.service;

import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.utility.Response;

public interface IPatientService {

	Response createPatient(PatientMaster patientDetails);

	Response updatePatient(PatientMaster patientDetails);

	Response getPatient(PatientMaster patientDetails);

	Response getPatientList();

	Response deletePatient(PatientMaster patientDetails);

	Response getPatientByLabId(PatientMaster patientDetails);


}
