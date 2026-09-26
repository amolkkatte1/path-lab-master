package pathlabmaster.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Test {

	public static void main(String[] args) {
		ZoneId ist = ZoneId.of("Asia/Kolkata");
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		System.out.println(now);

	}

}
