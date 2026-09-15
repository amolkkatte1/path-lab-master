package pathlabmaster.service;

import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.utility.Response;

public interface IClientConfigService {

	Response createClientConfig(ClientConfig clientConfigDetails);

	Response getClientConfigList();

	Response deleteClientConfig(ClientConfig clientConfigDetails);

	Response updateClientConfig(ClientConfig clientConfigDetails);

	Response getClientConfig(Long labId);
}
