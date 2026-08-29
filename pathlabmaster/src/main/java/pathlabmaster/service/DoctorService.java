package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.DoctorMasterRepository;
import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class DoctorService implements IDoctorService {

	@Autowired
	private DoctorMasterRepository doctorRepo;

	@Override
	public Response createDoctor(DoctorMaster doctorDetails) {
		doctorDetails.setDoctorId(Utility.generateId());
		doctorDetails.setUpdatedAt(Utility.getCurrentTime());
		doctorDetails.setCreatedAt(Utility.getCurrentTime());
		DoctorMaster savedDoctor = doctorRepo.save(doctorDetails);
		System.out.println(savedDoctor.getDoctorId()); 
		return new Response(ResponseStatus.success, 1, "Doctor created successfully", savedDoctor);
	}

	@Override
	public Response updateDoctor(DoctorMaster doctorDetails) {
		doctorDetails.setUpdatedAt(Utility.getCurrentTime());
		DoctorMaster savedDoctor = doctorRepo.save(doctorDetails);
		System.out.println(savedDoctor.getDoctorId()); 
		return new Response(ResponseStatus.success, 1, "Doctor Update successfully", savedDoctor);
	}
	
	@Override
	public Response getDoctor(DoctorMaster doctorDetails) {
		Optional<DoctorMaster> optionalDoctor = doctorRepo.findById(doctorDetails.getDoctorId());
		DoctorMaster Doctor = null;
		if (optionalDoctor.isPresent()) {
		    Doctor = optionalDoctor.get();
		} else {
		    throw new RuntimeException("Doctor not found");
		}
		return new Response(ResponseStatus.success, 1, "Get Doctor successfully", Doctor);
	}

	@Override
	public Response getDoctorList() {
		List<DoctorMaster> DoctorList = doctorRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Doctor List successfully", DoctorList);
	}

	@Override
	public Response deleteDoctor(DoctorMaster doctorDetails) {
		Optional<DoctorMaster> optionalDoctor = doctorRepo.findById(doctorDetails.getDoctorId());
		DoctorMaster Doctor = null;
		if (optionalDoctor.isPresent()) {
		    Doctor = optionalDoctor.get();
		} else {
		    throw new RuntimeException("Doctor not found");
		}
		doctorRepo.deleteById(doctorDetails.getDoctorId());
		return new Response(ResponseStatus.success, 1, "Delete Doctor successfully", Doctor);
	}

	@Override
	public Response getDoctorByLabId(DoctorMaster doctorDetails) {
		List<DoctorMaster> doctorList = doctorRepo.findByLabId(doctorDetails.getLabId());

		if (doctorList == null || doctorList.isEmpty()) {
			return new Response(ResponseStatus.success, 0, "No Doctor found for LabId: " + doctorDetails.getLabId(),
					doctorList);
		}

		return new Response(ResponseStatus.success, 1, "Get Doctor List by LabId successfully", doctorList);
	}

}
