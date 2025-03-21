package app.notification.client.dto;

import lombok.Data;

@Data
public class Notification {

    private String subject;

    private String status;

    private String type;
}