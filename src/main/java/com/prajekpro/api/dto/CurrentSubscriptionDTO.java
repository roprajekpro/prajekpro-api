package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProDetails;
import com.prajekpro.api.domain.ProSubscription;
import com.safalyatech.common.domains.PPLookUp;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CurrentSubscriptionDTO {

    private Long proSubscriptionId;
    private Long subscriptionId;
    private Double amount;
    private List<String> reference = new ArrayList<>();
    private String dateOfSubscription;
    private String dateOfRenewal;
    private Long daysLeftOfRenewal;
    private String transactionId;
    private String invoice;
    private int applicationNo;


    public CurrentSubscriptionDTO(ProDetails proDetails, ProSubscription currentProSubscription, List<PPLookUp> referenceList) {

        this.proSubscriptionId = currentProSubscription.getId();
        this.subscriptionId = currentProSubscription.getMasterSubscription().getId();
        this.amount = currentProSubscription.getMasterSubscription().getSubscriptionAmount();
        // this.reference = currentProSubscription.getSubscriptionDetails().getPpLookUp().getValue();
        this.dateOfSubscription = currentProSubscription.getDateOfSubscription();
        //this.transactionId = currentProSubscription.getWalletTransactionHistory().getTransactionId();
        this.transactionId = CommonUtility.convertTrxIdIntoString(currentProSubscription.getWalletTransactionHistory().getTransactionId());
        // this.invoice = currentProSubscription.getSubscriptionInvoice().getInvoiceNo();
        //TODO: Do correct mapping of Invoice no. for this
        if (CheckUtil.hasValue(currentProSubscription.getSubscriptionInvoices()))
            this.invoice = CommonUtility.convertInvoiceNoIntoString(currentProSubscription.getSubscriptionInvoices().get(0).getInvoiceNo());
        this.applicationNo = proDetails.getApplicationNo();

        if (!referenceList.isEmpty()) {
            for (PPLookUp lookup : referenceList) {
                this.reference.add(lookup.getValue());
            }
        }

       /* LocalDate date = LocalDate.parse(dateOfSubscription);
        log.debug("given tenure = {}",currentProSubscription.getTenure());
        LocalDate renewalDate = date.plusMonths(currentProSubscription.getTenure());*/

        this.dateOfRenewal = currentProSubscription.getSubscriptionExpiresOn();
        log.debug("dateOfRenewal = {}", dateOfRenewal);
        LocalDate renewalDate = LocalDate.parse(dateOfRenewal);
        LocalDate currentDate = LocalDate.now();
        this.daysLeftOfRenewal = ChronoUnit.DAYS.between(currentDate, renewalDate);
        log.debug("daysLeftOfRenewal = {}", daysLeftOfRenewal);

    }
}
