package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

// From RecruiterCommunication entity
@Data
public class RecruiterCommunicationDTO {
    private Long id;
    private Long recruiterId;
    private Long jobApplicationId;
    private LocalDateTime communicationDate;
    private String communicationMethod;  // Email, Phone, LinkedIn, InPerson
    private String communicationContent;
    private String nextAction;
    private LocalDateTime createdAt;
}
