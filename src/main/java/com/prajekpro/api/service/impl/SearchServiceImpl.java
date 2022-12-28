package com.prajekpro.api.service.impl;

import com.prajekpro.api.domain.ServiceItemCategory;
import com.prajekpro.api.domain.ServiceItemSubCategory;
import com.prajekpro.api.dto.ItemSubCategoryPricingDetailsDTO;
import com.prajekpro.api.dto.MetaData;
import com.prajekpro.api.dto.ProServiceItemsPricingDTO;
import com.prajekpro.api.repository.ServiceItemSubCategoryRepository;
import com.prajekpro.api.service.SearchService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.utility.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Throwable.class)
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ServiceItemSubCategoryRepository serviceItemSubCategoryRepository;

    @Override
    public BaseWrapper searchServices(String term) throws ServicesException {
        if (!CheckUtil.hasValue(term))
            throw new ServicesException("605");

        List<ProServiceItemsPricingDTO> searchedServiceItems = new ArrayList<>(0);

        //Search All Sub categories matching search term
        List<ServiceItemSubCategory> serviceItemSubCategories = serviceItemSubCategoryRepository.findByItemSubCategoryNameContainingIgnoreCaseAndActiveStatus(term, ActiveStatus.ACTIVE.value());

        if (CheckUtil.hasValue(serviceItemSubCategories)) {
            //Prepare a map of service subcategory items against it's category
            Map<ServiceItemCategory, ProServiceItemsPricingDTO> responseMap = new HashMap<>();

            //Map all subcategories search against the category
            for (ServiceItemSubCategory serviceItemSubCategory : serviceItemSubCategories) {
                ProServiceItemsPricingDTO proServiceItemsPricingDTO;
                ServiceItemCategory category = serviceItemSubCategory.getServiceItemCategory();
                if (responseMap.containsKey(category))
                    proServiceItemsPricingDTO = responseMap.get(category);
                else
                    proServiceItemsPricingDTO = new ProServiceItemsPricingDTO(category);

                //Update the new sub-category in iteration
                ItemSubCategoryPricingDetailsDTO itemSubCategoryPricingDetailsDTO = new ItemSubCategoryPricingDetailsDTO(serviceItemSubCategory);
                proServiceItemsPricingDTO.getItemSubCategoryDetails().add(itemSubCategoryPricingDetailsDTO);

                responseMap.put(category, proServiceItemsPricingDTO);
            }

            searchedServiceItems = responseMap.values().stream().collect(Collectors.toList());
        }
        return new BaseWrapper(new MetaData<>(searchedServiceItems));
    }
}
