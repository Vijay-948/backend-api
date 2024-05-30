package backend.real_estate.backendapi;

//import backend.real_estate.backendapi.service.impl.EmailSenderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//@EnableScheduling
//@EnableWebSecurity
public class BackendApiApplication {
//	@Autowired
//	private EmailSenderService service;

	public static void main(String[] args) {
		SpringApplication.run(BackendApiApplication.class, args);
	}

//	@EventListener(ApplicationReadyEvent.class)
//	public void triggerMail() throws MessagingException {
//
//		service.sendSimpleEmail("kvijayreddy948@gmail.com",
//				"This is Email Body with Attachment...",
//				"This email has attachment");
//
//	}
}
