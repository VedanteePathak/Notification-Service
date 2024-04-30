package org.adrij.common.Models.Entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "sms_requests_index")
@ToString
public class ElasticSMSDocument {
    @Id
    private long requestId;

    @Field(type = FieldType.Keyword, name = "phoneNumber")
    private String phoneNumber;

    @Field(type = FieldType.Text, name = "message")
    private String message;

    @Field(type = FieldType.Keyword, name = "status")
    private String status;

    @Field(type = FieldType.Keyword, name = "failureCode")
    private String failureCode;

    @Field(type = FieldType.Text, name = "failureComments")
    private String failureComments;

    @Field(type = FieldType.Date, name = "createdAt", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, name = "updatedAt", pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime updatedAt;

    public ElasticSMSDocument(SMSEntity smsEntity) {
        this.requestId = smsEntity.getRequestId();
        this.phoneNumber = smsEntity.getPhoneNumber();
        this.message = smsEntity.getMessage();
        this.status = smsEntity.getStatus();
        this.failureCode = smsEntity.getFailureCode();
        this.failureComments = smsEntity.getFailureComments();
        this.createdAt = smsEntity.getCreatedAt();
        this.updatedAt = smsEntity.getUpdatedAt();
    }
}