package com.prajekpro.api.service.impl;

import com.prajekpro.api.constants.ApplicationConstants;
import com.prajekpro.api.domain.TaxConfig;
import com.prajekpro.api.dto.CurrentLocationDetailsDTO;
import com.prajekpro.api.dto.LocationDetailsDTO;
import com.prajekpro.api.dto.TaxConfigDTO;
import com.prajekpro.api.repository.ProDetailsRepository;
import com.prajekpro.api.repository.TaxConfigRepository;
import com.prajekpro.api.service.AuthorizationService;
import com.prajekpro.api.service.LocationService;
import com.safalyatech.common.domains.PPLookUp;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.enums.Fields;
import com.safalyatech.common.enums.MasterDataKeys;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.repository.PPLookupRepository;
import com.safalyatech.common.repository.UsersRepository;
import com.safalyatech.common.utility.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Throwable.class)
public class LocationServiceImpl implements LocationService {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProDetailsRepository proDetailsRepository;

    @Override
    public BaseWrapper getUserLocationDetails() {
        Users userDetails = usersRepository.findById
                (authorizationService
                        .fetchLoggedInUser()
                        .getUserId())
                .get();
        return getUserLocationById(userDetails);
    }

    private BaseWrapper getUserLocationById(Users userDetails) {

//    	Float lat = null == userDetails.getLocLatitude() ? null : Float.parseFloat(userDetails.getLocLatitude());
//    	Float lng = null == userDetails.getLongitude() ? null : Float.parseFloat(userDetails.getLongitude());
        CurrentLocationDetailsDTO locationDetailsDTO = new CurrentLocationDetailsDTO(
                userDetails.getLocationText(),
                userDetails.getLocLatitude(),
                userDetails.getLocLongitude());

        return new BaseWrapper(locationDetailsDTO);
    }

    @Override
    public BaseWrapper getUserLocationDetails(Long proId) {
        Users users = proDetailsRepository.fetchByUserIdIn(proId);
        return getUserLocationById(users);
    }

    @Override
    public BaseWrapper updateUserLocation(LocationDetailsDTO request) throws ServicesException {
        Users loggedInUser = authorizationService.fetchLoggedInUser();
        return updateUserLocationById(loggedInUser, request);
    }

    @Override
    public BaseWrapper updateUserLocation(Long proId, LocationDetailsDTO request) throws ServicesException {
        Users users = proDetailsRepository.fetchByUserIdIn(proId);
        return updateUserLocationById(users, request);
    }

    private BaseWrapper updateUserLocationById(Users users, LocationDetailsDTO request) throws ServicesException {
        if (CheckUtil.hasValue(request)
                && CheckUtil.hasValue(request.getLatitude())
                && CheckUtil.hasValue(request.getLongitude())
                && CheckUtil.hasValue(request.getLocation())) {

            usersRepository.updateLocationDetails(
                    users.getUserId(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getLocation());

            return new BaseWrapper();
        } else
            throw new ServicesException("Invalid request supplied");
    }

    @Autowired
    private PPLookupRepository ppLookUpRepository;

    @Override
    public BaseWrapper getLookUpList(String reference, String term) throws Exception {

        List<PPLookUp> lookUpList = new ArrayList<>();
        Map<String, Object> finalLookUpMap = new HashMap<>();

        if (CheckUtil.hasValue(reference)) {

            List<String> referenceList = Arrays.asList(reference.split(ApplicationConstants.COMMA_DELIMITER));

            if (!referenceList.isEmpty()) {
//				log.debug("referenceList = {}", referenceList);

                // Group all the references to master
                groupLookupByMasterReference(finalLookUpMap, referenceList);

                // Fetch all references from look up table
                lookUpList = ppLookUpRepository.findByActiveStatusAndReferenceIn(
                        ActiveStatus.ACTIVE.value(),
                        referenceList,
                        Sort.by(Sort.Direction.ASC, Fields.value.name()));
            }
        } else {
            lookUpList = ppLookUpRepository.findByActiveStatus(
                    ActiveStatus.ACTIVE.value(),
                    Sort.by(Sort.Direction.ASC, Fields.value.name()));
        }

        //Put all look up data by reference list
        finalLookUpMap.putAll(
                groupLookUpByReference(lookUpList));
        return new BaseWrapper(finalLookUpMap);
    }

    @Autowired
    private TaxConfigRepository taxConfigRepository;

    private void groupLookupByMasterReference(Map<String, Object> finalLookUpMap, List<String> referenceList) {
        int referenceListSize = referenceList.size();

        for (int i = 0; i < referenceListSize; i++) {
            String referenceInIteration = referenceList.get(i);
            List<?> masterList = new ArrayList<>();
            if (CheckUtil.hasValue(referenceInIteration)
                    && MasterDataKeys.contains(referenceInIteration)) {
                switch (MasterDataKeys.valueOf(referenceInIteration)) {

                    case applicableTaxes:
                        List<TaxConfig> taxConfigList = taxConfigRepository.findByActiveStatus(ActiveStatus.ACTIVE.value());
                        masterList = taxConfigList.stream().map(TaxConfigDTO::new).collect(Collectors.toList());
                        break;

                    default:
                        break;
                }
            }

            if (!masterList.isEmpty())
                finalLookUpMap.put(referenceInIteration, masterList);
        }
    }

    private Map<String, List<PPLookUp>> groupLookUpByReference(List<PPLookUp> lookUpList) {

        Map<String, List<PPLookUp>> lookUpMap = new HashMap<>();

        lookUpList.forEach(
                (lookUp) -> {
                    String referenceName = lookUp.getReference();

                    List<PPLookUp> ctpLookUpForReferenceList = lookUpMap.get(referenceName);
                    if (!CheckUtil.hasValue(ctpLookUpForReferenceList))
                        ctpLookUpForReferenceList = new ArrayList<PPLookUp>();

                    ctpLookUpForReferenceList.add(lookUp);

                    lookUpMap.put(referenceName, ctpLookUpForReferenceList);
                });

        return lookUpMap;
    }
}
