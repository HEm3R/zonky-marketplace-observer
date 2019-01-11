package cz.chalupa.zonky.marketplace.observer.api;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;

import cz.chalupa.zonky.marketplace.observer.services.ObservationService;
import cz.chalupa.zonky.marketplace.observer.services.ObservationService.ObservationInfo;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/observation")
@AllArgsConstructor
@Validated
public class ObservationController {

    @NonNull private final ObservationService service;

    @GetMapping(path = "/latest")
    public ObservationInfo getLatestObservation(
            @RequestParam(value = "limit", defaultValue = "20") @Min(0) int limit,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) int offset) {

        return service.getObservation(limit, offset);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public static void handleConstraintViolations() {}
}
