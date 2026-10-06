package pathlabmaster.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.utility.Response;

public interface IHeaderFooterService {
	Response createHeaderFooter(Long labId, MultipartFile header, MultipartFile footer) throws IOException;
}
