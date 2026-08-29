package pathlabmaster.service;

import pathlabmaster.pojo.LabMaster;
import pathlabmaster.utility.Response;

public interface ILabService {

	Response createLab(LabMaster labDetails);

	Response updateLab(LabMaster labDetails);

	Response getLab(LabMaster labDetails);

	Response getLabList();

	Response deleteLab(LabMaster labDetails);

}
