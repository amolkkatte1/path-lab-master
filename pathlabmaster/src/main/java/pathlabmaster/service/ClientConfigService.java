package pathlabmaster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.ClientConfigRepository;
import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class ClientConfigService implements IClientConfigService {

	@Autowired
	private ClientConfigRepository ClientConfigRepo;

	@Override
	public Response createClientConfig(ClientConfig clientConfigDetails) {
		clientConfigDetails.setConfigId(Utility.generateId());
		clientConfigDetails.setUpdatedAt(Utility.getCurrentTime());
		clientConfigDetails.setCreatedAt(Utility.getCurrentTime());
		ClientConfig savedClientConfig = ClientConfigRepo.save(clientConfigDetails);
		System.out.println(savedClientConfig.getConfigId()); 
		return new Response(ResponseStatus.success, 1, "ClientConfig created successfully", savedClientConfig);
	}

	@Override
	public Response updateClientConfig(ClientConfig clientConfigDetails) {
		clientConfigDetails.setUpdatedAt(Utility.getCurrentTime());
		ClientConfig savedClientConfig = ClientConfigRepo.save(clientConfigDetails);
		System.out.println(savedClientConfig.getConfigId()); 
		return new Response(ResponseStatus.success, 1, "ClientConfig Update successfully", savedClientConfig);
	}
	
	@Override
	public Response getClientConfig(Long labId) {
		ClientConfig clientConfig = ClientConfigRepo.findByLabId(labId);
		return new Response(ResponseStatus.success, 1, "Get ClientConfig successfully", clientConfig);
	}

	@Override
	public Response getClientConfigList() {
		List<ClientConfig> ClientConfigList = ClientConfigRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get ClientConfig List successfully", ClientConfigList);
	}

	@Override
	public Response deleteClientConfig(ClientConfig clientConfigDetails) {
		clientConfigDetails = ClientConfigRepo.findByLabId(clientConfigDetails.getLabId());
		ClientConfigRepo.deleteById(clientConfigDetails.getConfigId());
		return new Response(ResponseStatus.success, 1, "Delete ClientConfig successfully", clientConfigDetails);
	}

	
}
