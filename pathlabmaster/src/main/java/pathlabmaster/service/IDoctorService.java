package pathlabmaster.service;

import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.utility.Response;

public interface IDoctorService {

	Response createDoctor(DoctorMaster doctorDetails);

	Response updateDoctor(DoctorMaster doctorDetails);

	Response getDoctor(DoctorMaster doctorDetails);

	Response getDoctorList();

	Response deleteDoctor(DoctorMaster doctorDetails);

}
