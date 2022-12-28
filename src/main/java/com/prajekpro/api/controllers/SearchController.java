package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.service.SearchService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Search API's")
@RestController
@RequestMapping(value = {RestUrlConstants.PP_SEARCH})
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping(value = {RestUrlConstants.PP_SERVICES})
    @ApiOperation(value = "Api to search for services subcategories based on term")
    public BaseWrapper searchServiceSubCategories(@RequestParam(value = "term", required = true) String term) throws ServicesException {
        return searchService.searchServices(term);
    }
}
