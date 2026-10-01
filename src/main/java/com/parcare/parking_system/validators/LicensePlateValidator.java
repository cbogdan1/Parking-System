package com.parcare.parking_system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LicensePlateValidator implements ConstraintValidator<ValidLicensePlate, String> {

    private static final String LICENSE_PLATE_REGEX =
            "^(B|AB|AG|AR|BC|BH|BN|BR|BT|BV|BZ|CJ|CL|CS|CT|CV|DB|DJ|GJ|GL|GR|HD|HR|IF|IL|IS|MH|MM|MS|NT|OT|PH|SB|SJ|SM|SV|TL|TM|TR|VL|VN|VS)-(\\d{2,3})-([A-Z]{3})$";

    @Override
    public void initialize(ValidLicensePlate constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String licensePlate, ConstraintValidatorContext constraintValidatorContext) {
        if (licensePlate == null || licensePlate.isEmpty()) {
            return false;
        }
        return licensePlate.matches(LICENSE_PLATE_REGEX);
    }
}
