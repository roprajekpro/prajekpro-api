package com.prajekpro.api.dto;

import com.prajekpro.api.domain.Currency;
import com.safalyatech.common.domains.PPLookUp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MasterSubscriptionDTO {
    private Long id;
    private Double subscriptionAmount;
    private Float subscriptionTenure;
    private Currency currency;
    private List<String> reference = new ArrayList<>();


    public MasterSubscriptionDTO(List<PPLookUp> referenceList) {
        if (!referenceList.isEmpty()) {
            for (PPLookUp lookup : referenceList) {
                this.reference.add(lookup.getValue());
            }
        }

    }
}
