package com.prajekpro.api.service;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.service.impl.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;

import java.util.List;

public interface DTOFactory {

    AdvertisementDTO createAdvertisementDTO(Advertisements advertisements);

    ProDetailsDTO createProDetailsDTO(ProDetails proDetails);

    ProReviewsDTO createProReviewsDTO(ProReviews input);

    ItemSubCategoryPricingDetailsDTO createItemSubCategoryPricingDetailsDTO(ProServiceItemsPricing input);

    MasterSubscriptionDTO createMasterSubscriptionDTO(MasterSubscription input, List<PPLookUp> referenceList);

    ProServicesDTO createProServiceDTO(Services input);

    ProServiceDocumentDetailsDTO createProServiceDocumentDetailsDTO(ProServiceDocuments input);

    ProServiceCommentsDTO createProServiceCommentsDTO(ProServiceComments input);
}
