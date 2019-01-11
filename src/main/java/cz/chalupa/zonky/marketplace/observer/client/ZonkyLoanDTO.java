package cz.chalupa.zonky.marketplace.observer.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@ToString
@JsonDeserialize(builder = ZonkyLoanDTO.ZonkyLoanDTOBuilder.class)
public class ZonkyLoanDTO {

    private final Long id;
    private final String url;
    private final String name;
    private final String story;
    private final String purpose;
    private final List<PhotoDTO> photos;
    private final String nickName;
    private final Integer termInMonths;
    private final Double interestRate;
    private final String rating;
    private final Boolean topped;
    private final BigDecimal amount;
    private final BigDecimal remainingInvestment;
    private final BigDecimal reservedAmount;
    private final Double investmentRate;
    private final Boolean covered;
    private final ZonedDateTime datePublished;
    private final Boolean published;
    private final ZonedDateTime deadline;
    private final Integer investmentsCount;
    private final Integer questionsCount;
    private final String region;
    private final String mainIncomeType;
    private final Boolean insuranceActive;
    private final List<InsuranceHistoryDTO> insuranceHistory;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ZonkyLoanDTOBuilder {}

    @Builder
    @ToString
    @JsonDeserialize(builder = PhotoDTO.PhotoDTOBuilder.class)
    public static class PhotoDTO {

        @NonNull private String name;
        @NonNull private String url;

        @JsonPOJOBuilder(withPrefix = "")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static final class PhotoDTOBuilder {}
    }

    @Builder
    @ToString
    @JsonDeserialize(builder = InsuranceHistoryDTO.InsuranceHistoryDTOBuilder.class)
    public static class InsuranceHistoryDTO {

        @NonNull private LocalDate policyPeriodFrom;
        @NonNull private LocalDate policyPeriodTo;

        @JsonPOJOBuilder(withPrefix = "")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static final class InsuranceHistoryDTOBuilder {}
    }
}
