package org.astrosearcher.unit.input;

import org.astrosearcher.classes.constants.Limits;
import org.astrosearcher.classes.constants.cds.SimbadConstants;
import org.astrosearcher.classes.constants.messages.ValidationMSG;
import org.astrosearcher.models.SearchFormInput;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

public class SearchFormInputTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final int    NEGATIVE_PAGE   = -42;
    private final int    ZERO_PAGE       =   0;
    private final int    VALID_PAGE      =  25;

    private final int    NEGATIVE_PAGESIZE = -99;
    private final int    ZERO_PAGESIZE     =   0;
    private final int    VALID_PAGESIZE    = 250;

    @Test
    public void notPositivePageShouldNotPass() {
        Set<ConstraintViolation<SearchFormInput>> violations;

        violations = validator.validate(
                new SearchFormInput("", "", SimbadConstants.DEFAULT_FORMAT, Limits.DEFAULT_RADIUS,
                        NEGATIVE_PAGE, VALID_PAGESIZE,
                        "code", "V/139", null, true, true, true));

        if ( violations.size() != 1) Assertions.fail();
        if ( !violations.iterator().next().getMessage().equals(ValidationMSG.PAGE_MIN_VALIDATION_MSG) ) {
            Assertions.fail();
        }

        violations = validator.validate(
                new SearchFormInput("", "", SimbadConstants.DEFAULT_FORMAT,  Limits.DEFAULT_RADIUS,
                        ZERO_PAGE, VALID_PAGESIZE,
                        "code",  "V/139", null, true, true, true));

        if ( violations.size() != 1) Assertions.fail();
        if ( !violations.iterator().next().getMessage().equals(ValidationMSG.PAGE_MIN_VALIDATION_MSG) ) {
            Assertions.fail();
        }
    }

    @Test
    public void notPositivePagesizeShouldNotPass() {
        Set<ConstraintViolation<SearchFormInput>> violations;

        violations = validator.validate(
                new SearchFormInput("", "", SimbadConstants.DEFAULT_FORMAT,  Limits.DEFAULT_RADIUS,
                        VALID_PAGE, NEGATIVE_PAGESIZE,
                        "code",  "V/139", null, true, true, true));

        if ( violations.size() != 1) Assertions.fail();
        if ( !violations.iterator().next().getMessage().equals(ValidationMSG.PAGESIZE_MIN_VALIDATION_MSG) ) {
            Assertions.fail();
        }

        violations = validator.validate(
                new SearchFormInput("", "", SimbadConstants.DEFAULT_FORMAT,  Limits.DEFAULT_RADIUS,
                        VALID_PAGE, ZERO_PAGESIZE,
                        "code", "V/139", null, true, true, true));

        if ( violations.size() != 1) Assertions.fail();
        if ( !violations.iterator().next().getMessage().equals(ValidationMSG.PAGESIZE_MIN_VALIDATION_MSG) ) {
            Assertions.fail();
        }
    }
}
