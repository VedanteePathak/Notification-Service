package org.vedantee.common.Models.DTOs.Requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vedantee.common.Models.DTOs.Requests.Validators.ValidPhoneNumber;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class SMSRequestDTO {
    @JsonProperty("phone_number")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Message cannot be empty.")
    @JsonProperty("message")
    private String message;
}
