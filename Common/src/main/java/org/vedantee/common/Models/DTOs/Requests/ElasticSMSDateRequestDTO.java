package org.vedantee.common.Models.DTOs.Requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vedantee.common.Models.DTOs.Requests.Validators.BeginBeforeEnd;
import org.vedantee.common.Models.DTOs.Requests.Validators.ValidPhoneNumber;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
@BeginBeforeEnd
public class ElasticSMSDateRequestDTO {
    @ValidPhoneNumber
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull(message = "Begin DateTime cannot be empty.")
    @Past(message = "Begin DateTime cannot be from the future.")
    @JsonProperty("begin")
    private LocalDateTime begin;

    @NotNull(message = "End DateTime cannot be empty.")
    @Past(message = "End DateTime cannot be from the future.")
    @JsonProperty("end")
    private LocalDateTime end;
}
