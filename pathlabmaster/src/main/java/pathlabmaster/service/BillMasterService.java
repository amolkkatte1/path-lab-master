package pathlabmaster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.BillMasterRepository;
import pathlabmaster.pojo.BillMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class BillMasterService implements IBillMasterService {

	@Autowired
	private BillMasterRepository billMasterRepo;

	@Override
	public Response createBillMaster(BillMaster BillMasterDetails) {
		BillMasterDetails.setBillId(Utility.generateId());
		BillMasterDetails.setUpdatedAt(Utility.getCurrentTime());
		BillMasterDetails.setCreatedAt(Utility.getCurrentTime());
		BillMaster savedBillMaster = billMasterRepo.save(BillMasterDetails);
		System.out.println(savedBillMaster.getBillId()); 
		return new Response(ResponseStatus.success, 1, "BillMaster created successfully", savedBillMaster);
	}

	@Override
	public Response updateBillMaster(BillMaster BillMasterDetails) {
		BillMasterDetails.setUpdatedAt(Utility.getCurrentTime());
		BillMaster savedBillMaster = billMasterRepo.save(BillMasterDetails);
		System.out.println(savedBillMaster.getBillId()); 
		return new Response(ResponseStatus.success, 1, "BillMaster Update successfully", savedBillMaster);
	}
	
	@Override
	public Response getBillMaster(Long labId) {
		BillMaster BillMaster = billMasterRepo.findByLabId(labId);
		return new Response(ResponseStatus.success, 1, "Get BillMaster successfully", BillMaster);
	}

	@Override
	public Response getBillMasterList() {
		List<BillMaster> BillMasterList = billMasterRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get BillMaster List successfully", BillMasterList);
	}

	@Override
	public Response deleteBillMaster(BillMaster BillMasterDetails) {
		billMasterRepo.deleteById(BillMasterDetails.getBillId());
		return new Response(ResponseStatus.success, 1, "Delete BillMaster successfully", BillMasterDetails);
	}

	@Override
	public Response getBillMasterByLabIdAndPatientId(Long labId, Long patientId) {
		BillMaster billMaster = billMasterRepo.findByPatientIdAndLabId(patientId,labId);
		return new Response(ResponseStatus.success, 1, "Get BillMaster List successfully", billMaster);
	}

	
}
