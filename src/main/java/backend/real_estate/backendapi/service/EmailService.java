package backend.real_estate.backendapi.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
