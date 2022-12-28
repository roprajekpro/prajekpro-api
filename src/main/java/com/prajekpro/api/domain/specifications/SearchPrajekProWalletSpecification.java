package com.prajekpro.api.domain.specifications;

import com.prajekpro.api.domain.PrajekProWalletDetails;
import com.prajekpro.api.dto.PrajekProWalletSearchRequestDTO;
import com.safalyatech.common.utility.CheckUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SearchPrajekProWalletSpecification implements Specification<PrajekProWalletDetails> {

    private PrajekProWalletSearchRequestDTO requestDTO;

    public SearchPrajekProWalletSpecification(PrajekProWalletSearchRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

    @Override
    public Predicate toPredicate(Root<PrajekProWalletDetails> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        prepareWalletAmountTypePredicate(requestDTO, root, criteriaBuilder, predicates);
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private void prepareWalletAmountTypePredicate(PrajekProWalletSearchRequestDTO requestDTO, Root<PrajekProWalletDetails> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (CheckUtil.hasValue(requestDTO.getWalletAmountTypes()) || !requestDTO.getWalletAmountTypes().isEmpty()) {
            predicates.add(criteriaBuilder.and(root.get("walletAmountType").in(requestDTO.getWalletAmountTypes())));
        }

    }
}
