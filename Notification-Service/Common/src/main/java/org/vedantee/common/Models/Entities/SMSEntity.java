package org.adrij.common.Models.Entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "sms_requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMSEntity extends BaseEntity {
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "failure_code")
    private String failureCode;

    @Column(name = "failure_comments")
    private String failureComments;
}
