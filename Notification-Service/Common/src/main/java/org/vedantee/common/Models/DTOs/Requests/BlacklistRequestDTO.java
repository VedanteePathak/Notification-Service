package org.adrij.common.Models.DTOs.Requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.adrij.common.Models.DTOs.Requests.Validators.PhoneNumberConstraint;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@Validated
public class BlacklistRequestDTO {
    @Size(min = 1, message = "List must contain at least one phone number.")
    @JsonProperty("phone_numbers")
    @PhoneNumberConstraint
    @Size(max = 100, message = "Cannot blacklist/whitelist more than 100 phone numbers at a time.")
    private ArrayList<@Valid String> phoneNumbers;

    @JsonCreator
    public BlacklistRequestDTO(@JsonProperty("phone_numbers") ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
