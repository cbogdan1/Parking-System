package com.parcare.parking_system.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LicensePlateValidator.class)
public @interface ValidLicensePlate {

    String message() default "Numarul de inmatriculare este invalid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
