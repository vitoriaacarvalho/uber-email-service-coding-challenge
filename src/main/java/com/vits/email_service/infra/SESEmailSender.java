package com.vits.email_service.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.vits.email_service.adapters.EmailSenderGateway;
import com.vits.email_service.core.exceptions.EmailServiceException;

@Service
public class SESEmailSender implements EmailSenderGateway{
	private final AmazonSimpleEmailService amazonSES;
	
	private String fromEmail = System.getenv("FROM_EMAIL");
	
	@Autowired
	public SESEmailSender(AmazonSimpleEmailService amazonSES) {
		this.amazonSES = amazonSES;
	}
	@Override
	public void sendEmail(String to, String subject, String body) {
		SendEmailRequest request = new SendEmailRequest()
				.withSource(fromEmail)
				.withDestination(new Destination().withToAddresses(to))
				.withMessage(new Message()
						.withSubject(new Content(subject))
						.withBody(new Body().withText(new Content(body)))
				);				
		try {
			this.amazonSES.sendEmail(request);
		} catch(AmazonServiceException e) {
			throw new EmailServiceException("Failure while sending email." + e.getMessage());
		}
	}

}
