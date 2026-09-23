package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.MdDoctorMasterRepository;
import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class MdDoctorService implements IMdDoctorService {

	@Autowired
	private MdDoctorMasterRepository MdDoctorRepo;

	@Override
	public Response createMdDoctor(MdDoctorMaster MdDoctorDetails) {
		MdDoctorDetails.setMdDoctorId(Utility.generateId());
		MdDoctorDetails.setUpdatedAt(Utility.getCurrentTime());
		MdDoctorDetails.setCreatedAt(Utility.getCurrentTime());
		MdDoctorMaster savedMdDoctor = MdDoctorRepo.save(MdDoctorDetails);
		System.out.println(savedMdDoctor.getMdDoctorId()); 
		return new Response(ResponseStatus.success, 1, "MdDoctor created successfully", savedMdDoctor);
	}

	@Override
	public Response updateMdDoctor(MdDoctorMaster MdDoctorDetails) {
		MdDoctorDetails.setUpdatedAt(Utility.getCurrentTime());
		MdDoctorMaster savedMdDoctor = MdDoctorRepo.save(MdDoctorDetails);
		System.out.println(savedMdDoctor.getMdDoctorId()); 
		return new Response(ResponseStatus.success, 1, "MdDoctor Update successfully", savedMdDoctor);
	}
	
	@Override
	public Response getMdDoctor(MdDoctorMaster MdDoctorDetails) {
		Optional<MdDoctorMaster> optionalMdDoctor = MdDoctorRepo.findById(MdDoctorDetails.getMdDoctorId());
		MdDoctorMaster MdDoctor = null;
		if (optionalMdDoctor.isPresent()) {
		    MdDoctor = optionalMdDoctor.get();
		} else {
		    throw new RuntimeException("MdDoctor not found");
		}
		return new Response(ResponseStatus.success, 1, "Get MdDoctor successfully", MdDoctor);
	}

	@Override
	public Response getMdDoctorList() {
		List<MdDoctorMaster> MdDoctorList = MdDoctorRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get MdDoctor List successfully", MdDoctorList);
	}

	@Override
	public Response deleteMdDoctor(MdDoctorMaster MdDoctorDetails) {
		Optional<MdDoctorMaster> optionalMdDoctor = MdDoctorRepo.findById(MdDoctorDetails.getMdDoctorId());
		MdDoctorMaster MdDoctor = null;
		if (optionalMdDoctor.isPresent()) {
		    MdDoctor = optionalMdDoctor.get();
		} else {
		    throw new RuntimeException("MdDoctor not found");
		}
		MdDoctorRepo.deleteById(MdDoctorDetails.getMdDoctorId());
		return new Response(ResponseStatus.success, 1, "Delete MdDoctor successfully", MdDoctor);
	}

	@Override
	public Response getMdDoctorByLabId(Long labId) {
		List<MdDoctorMaster> MdDoctorList = MdDoctorRepo.findByLabId(labId);

		if (MdDoctorList == null || MdDoctorList.isEmpty()) {
			return new Response(ResponseStatus.success, 0, "No MdDoctor found for LabId: " + labId,
					MdDoctorList);
		}

		return new Response(ResponseStatus.success, 1, "Get MdDoctor List by LabId successfully", MdDoctorList);
	}

}
