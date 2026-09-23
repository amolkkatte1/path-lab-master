package pathlabmaster.service;

import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.utility.Response;

public interface IMdDoctorService {

	Response createMdDoctor(MdDoctorMaster MdDoctorDetails);

	Response updateMdDoctor(MdDoctorMaster MdDoctorDetails);

	Response getMdDoctor(MdDoctorMaster MdDoctorDetails);

	Response getMdDoctorList();

	Response deleteMdDoctor(MdDoctorMaster MdDoctorDetails);

	Response getMdDoctorByLabId(Long labId);

}
