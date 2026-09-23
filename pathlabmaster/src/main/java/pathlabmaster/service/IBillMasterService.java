package pathlabmaster.service;

import pathlabmaster.pojo.BillMaster;
import pathlabmaster.utility.Response;

public interface IBillMasterService {

	Response createBillMaster(BillMaster BillMasterDetails);

	Response getBillMasterList();

	Response deleteBillMaster(BillMaster BillMasterDetails);

	Response updateBillMaster(BillMaster BillMasterDetails);

	Response getBillMaster(Long labId);
}
