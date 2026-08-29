package pathlabmaster.service;

import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.utility.Response;

public interface IDoctorService {

	Response createDoctor(DoctorMaster doctorDetails);

	Response updateDoctor(DoctorMaster doctorDetails);

	Response getDoctor(DoctorMaster doctorDetails);

	Response getDoctorList();

	Response deleteDoctor(DoctorMaster doctorDetails);

	Response getDoctorByLabId(DoctorMaster doctorDetails);

}
