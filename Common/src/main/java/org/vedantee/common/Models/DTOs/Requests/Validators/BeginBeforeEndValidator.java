package org.vedantee.common.Models.DTOs.Requests.Validators;

import org.vedantee.common.Models.DTOs.Requests.ElasticSMSDateRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BeginBeforeEndValidator implements ConstraintValidator<BeginBeforeEnd, ElasticSMSDateRequestDTO> {
    @Override
    public boolean isValid(ElasticSMSDateRequestDTO elasticSMSDataRequestDTO,
                           ConstraintValidatorContext context) {
        if (elasticSMSDataRequestDTO.getBegin() == null
                || elasticSMSDataRequestDTO.getEnd() == null) {
            return false;
        }
        return elasticSMSDataRequestDTO.getBegin().isBefore(elasticSMSDataRequestDTO.getEnd());
    }
}
