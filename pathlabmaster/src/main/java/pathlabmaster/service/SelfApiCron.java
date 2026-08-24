package pathlabmaster.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;

public class SelfApiCron {

    private final RestClient restClient;

    public SelfApiCron(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void callSelfApi() {

        try {
            String response = restClient.get()
                    .uri("https://path-lab-master.onrender.com/user/")
                    .retrieve()
                    .body(String.class);

            System.out.println("Self API called successfully: " + response);

        } catch (Exception e) {
            System.err.println("Self API call failed: " + e.getMessage());
        }
    }
}