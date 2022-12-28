package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.LocationDetailsDTO;
import com.prajekpro.api.service.LocationService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_LOCATION_DATA
})
@Api(value = "Api to fetch or update location details for any user of PP")
public class LocationController {


    @Autowired
    private LocationService locationService;


    @ApiOperation(value = "Api to get user last updated location details")
    @GetMapping
    public BaseWrapper getUserLocationDetails() {

        return locationService.getUserLocationDetails();
    }


    @ApiOperation(value = "Api to update user location details")
    @PutMapping
    public BaseWrapper updateUserLocation(
            @RequestBody LocationDetailsDTO request) throws ServicesException {

        return locationService.updateUserLocation(request);
    }
}
