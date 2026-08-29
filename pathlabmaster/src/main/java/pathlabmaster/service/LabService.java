package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.LabMasterRepository;
import pathlabmaster.pojo.LabMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class LabService implements ILabService {

	@Autowired
	private LabMasterRepository labRepo;

	@Override
	public Response createLab(LabMaster labDetails) {
		labDetails.setLabId(Utility.generateId());
		labDetails.setUpdatedAt(Utility.getCurrentTime());
		labDetails.setCreatedAt(Utility.getCurrentTime());
		LabMaster savedLab = labRepo.save(labDetails);
		System.out.println(savedLab.getLabId()); 
		return new Response(ResponseStatus.success, 1, "Lab created successfully", savedLab);
	}

	@Override
	public Response updateLab(LabMaster labDetails) {
		labDetails.setUpdatedAt(Utility.getCurrentTime());
		LabMaster savedLab = labRepo.save(labDetails);
		System.out.println(savedLab.getLabId()); 
		return new Response(ResponseStatus.success, 1, "Lab Update successfully", savedLab);
	}
	
	@Override
	public Response getLab(LabMaster labDetails) {
		Optional<LabMaster> optionalLab = labRepo.findById(labDetails.getLabId());
		LabMaster Lab = null;
		if (optionalLab.isPresent()) {
		    Lab = optionalLab.get();
		} else {
		    throw new RuntimeException("Lab not found");
		}
		return new Response(ResponseStatus.success, 1, "Get Lab successfully", Lab);
	}

	@Override
	public Response getLabList() {
		List<LabMaster> LabList = labRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Lab List successfully", LabList);
	}

	@Override
	public Response deleteLab(LabMaster labDetails) {
		Optional<LabMaster> optionalLab = labRepo.findById(labDetails.getLabId());
		LabMaster Lab = null;
		if (optionalLab.isPresent()) {
		    Lab = optionalLab.get();
		} else {
		    throw new RuntimeException("Lab not found");
		}
		labRepo.deleteById(labDetails.getLabId());
		return new Response(ResponseStatus.success, 1, "Delete Lab successfully", Lab);
	}

	
}
