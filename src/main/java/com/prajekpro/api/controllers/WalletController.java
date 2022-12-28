package com.prajekpro.api.controllers;

import com.adyen.service.exception.ApiException;
import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.domain.specifications.SearchPrajekProWalletSpecification;
import com.prajekpro.api.dto.PrajekProWalletSearchRequestDTO;
import com.prajekpro.api.dto.WalletAddAmountDTO;
import com.prajekpro.api.service.WalletService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_WALLET
})
@Api(value = "API's to perform operations specific to Wallet info")
public class WalletController {

    @Autowired
    private WalletService walletService;


    @ApiOperation(value = "API to get wallet details and Transaction History")
    @GetMapping()
    public BaseWrapper getWalletDetails(Pageable pageable) throws ServicesException {
        return walletService.getWalletDetailsByProId(pageable);
    }

    @ApiOperation(value = "API to get wallet Top up History")
    @GetMapping(value = {RestUrlConstants.PP_WALLET_TOPUPHISTORY})
    public BaseWrapper getWalletTopUpHistory(Pageable pageable) throws ServicesException {
        return walletService.getWalletTopUpHistory(pageable);
    }

    @ApiOperation(value = "API to Add amount in Wallet")
    @PutMapping()
    public BaseWrapper addAmountInWallet(@RequestBody WalletAddAmountDTO request) throws ServicesException, IOException, ApiException {
        return walletService.addWalletAmount(request);

    }

    @ApiOperation(value = " API to get PrajekPro Wallet details list get API")
    @GetMapping(value = {RestUrlConstants.PP_PRAJEKPRO_WALLET_HISTORY})
    public BaseWrapper getPrajekProWallethstory(Pageable pageable){
        return walletService.getPrajekProWalletList(null,pageable);
    }

    @ApiOperation(value = " API to get PrajekPro Wallet details list post API")
    @PostMapping(value = {RestUrlConstants.PP_PRAJEKPRO_WALLET_HISTORY})
    public BaseWrapper getPrajekProWalletWithFilter(@RequestBody PrajekProWalletSearchRequestDTO request, Pageable pageable){
        return walletService.getPrajekProWalletList(request,pageable);
    }
}
