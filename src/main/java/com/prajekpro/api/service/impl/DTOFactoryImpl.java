package com.prajekpro.api.service.impl;

import com.prajekpro.api.constants.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.service.*;
import com.prajekpro.api.util.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.repository.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.util.*;

@Service
@Transactional(rollbackOn = Throwable.class)
public class DTOFactoryImpl implements DTOFactory {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public AdvertisementDTO createAdvertisementDTO(Advertisements advertisements) {

        AdvertisementDTO advertisementDTO = new AdvertisementDTO(advertisements);
        // BeanUtils.copyProperties(advertisements, advertisementDTO);
        return advertisementDTO;
    }

    @Override
    public ProDetailsDTO createProDetailsDTO(ProDetails proDetails) {

        Optional<AvailabilityStatus> aStatusOptional = AvailabilityStatus.valueOf(
                proDetails.getAvailabilityStatus());
        String totalJobsCompleted = CommonUtil.convertTotalJobsCompleted(proDetails.getTotalJobsCompleted(), PPConstants.JOBS_DISPLAY_MULTIPLES);
        ProDetailsDTO proDetailsDTO = new ProDetailsDTO(
                proDetails.getId(),
                proDetails.getUserDetails().getUserId(),
                proDetails.getUserDetails().getName(),
                proDetails.getUserDetails().getPrflImgDisplayNm(),
                proDetails.getStartingCost(),
                proDetails.getStartingCostCurrency(),
                proDetails.getRatings(),
                proDetails.getRatedBy(),
                proDetails.getExperienceInYears(),
                totalJobsCompleted,
                aStatusOptional.isPresent()
                        ? aStatusOptional.get().name().toLowerCase()
                        : AvailabilityStatus.UNAVAILABLE.name().toLowerCase(),
                proDetails.getAboutText(),
                false,
                false);

        return proDetailsDTO;
    }

    @Override
    public ProReviewsDTO createProReviewsDTO(ProReviews proReviews) {

        ProReviewsDTO proReviewsDTO = new ProReviewsDTO(proReviews);
        return proReviewsDTO;
    }

    @Override
    public ItemSubCategoryPricingDetailsDTO createItemSubCategoryPricingDetailsDTO(ProServiceItemsPricing input) {

        ItemSubCategoryPricingDetailsDTO itemSubCategoryPricingDetailsDTO = new ItemSubCategoryPricingDetailsDTO(input);
        return itemSubCategoryPricingDetailsDTO;
    }

    @Override
    public MasterSubscriptionDTO createMasterSubscriptionDTO(MasterSubscription input, List<PPLookUp> referenceList) {
        MasterSubscriptionDTO masterSubscriptionDTO = new MasterSubscriptionDTO(referenceList);
        BeanUtils.copyProperties(input, masterSubscriptionDTO);
        return masterSubscriptionDTO;
    }

    @Override
    public ProServicesDTO createProServiceDTO(Services input) {
        return new ProServicesDTO(input.getId(), input.getServiceName(), input.getServiceIcon());
    }

    @Override
    public ProServiceDocumentDetailsDTO createProServiceDocumentDetailsDTO(ProServiceDocuments input) {
        ProServiceDocumentDetailsDTO proServiceDocumentDetailsDTO = new ProServiceDocumentDetailsDTO();
        proServiceDocumentDetailsDTO.setDocId(input.getId());
        proServiceDocumentDetailsDTO.setDocName(input.getFileName());
        proServiceDocumentDetailsDTO.setDocType(input.getDocType().name());
        proServiceDocumentDetailsDTO.setUploadDt(input.getUploadDt());

        return proServiceDocumentDetailsDTO;
    }

    @Override
    public ProServiceCommentsDTO createProServiceCommentsDTO(ProServiceComments input) {
        ProServiceCommentsDTO proServiceCommentsDTO = new ProServiceCommentsDTO();
        proServiceCommentsDTO.setId(input.getId());
        proServiceCommentsDTO.setProId(input.getProDetails().getId());
        proServiceCommentsDTO.setServiceId(input.getService().getId());

        Users users = usersRepository.fetchByUserIdAndActiveStatusIn(input.getCreatedBy(), Arrays.asList(ActiveStatus.ACTIVE.value()));
        proServiceCommentsDTO.setCommentedBy(users.getFullName());

        proServiceCommentsDTO.setSubject(input.getSubject());
        proServiceCommentsDTO.setComment(input.getComment());
        proServiceCommentsDTO.setCommentedTs(input.getModifiedTs());

        return proServiceCommentsDTO;
    }
}
