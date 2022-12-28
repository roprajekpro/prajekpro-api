package com.prajekpro.api.service.impl;

import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.util.*;


@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class CommonServiceImpl implements CommonService {

    @Autowired
    private ProService proService;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private PrajekproWalletDetailsRepository prajekproWalletDetailsRepository;


    @Override
    public BaseWrapper getUsers() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        log.debug("Logged In UserInfo from securityContext = {}", user.toString());
        String userId = user.getUserId();

        Users userInfo = usersRepository.fetchByUserIdAndActiveStatusIn(userId, Arrays.asList(ActiveStatus.ACTIVE.value(),
                ActiveStatus.APPROVAL_PENDING.value(),
                ActiveStatus.REASSESSMENT.value(),
                ActiveStatus.SUBSCRIPTION_EXPIRED.value()));

        Set<RoleMasterDtl> roles = userInfo.getRoles();

        for (RoleMasterDtl roleMasterDtl : roles) {
            if ((roleMasterDtl.getRoles()).equals(Roles.ROLE_VENDOR.name())) {
                log.debug("inside prolisting services in user info");
                ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userInfo.getUserId());
                Long proId = proDetails.getId();
                userInfo.setProId(proId);
                userInfo.setAvailabilityStatus(proDetails.getAvailabilityStatus());
                userInfo.setVatRegistered(proDetails.isVatRegistered());
                userInfo.setVatNo(proDetails.getVatNo());
                userInfo.setExperienceInYears(proDetails.getExperienceInYears());
                userInfo.setAboutText(proDetails.getAboutText());

                List<ProvidedServiceDetailsDTO> providedServiceDetailsDTOList = proService.getProServices(proId, 0l);
                userInfo.setProServices(providedServiceDetailsDTOList);

                //documents set is added into pro basic details
                List<DocumentsDTO> documents = new ArrayList<>();
                for (ProDocuments document : proDetails.getDocuments()) {
                    DocumentsDTO doc = new DocumentsDTO();
                    doc.setType(document.getType().value());
                    doc.setUrl(document.getUrl());
                    doc.setId(document.getId());
                    String url = document.getUrl();
                    if (url.contains("/")) {
                        doc.setSavedName(url.substring(url.lastIndexOf("/")));
                    }
                    documents.add(doc);
                }
                userInfo.setProDocuments(documents);

            }
        }


        //TODO: Remove this workaround for lazy initialization of UserPermission
//		List<UserPermissions> userPermissions = userInfo.getUserPermissions();
//		log.debug("userPermissions = {}", userPermissions.toString());

        return new BaseWrapper(userInfo);
    }

    @Override
    public BaseWrapper getDashboardInfo() throws ServicesException {

        Long totalAppointments = 0l;
        Long totalBookedAppointments = 0l;
        Long totalConfirmedAppointments = 0l;
        Long totalCompletedAppointments = 0l;
        Long totalCancelledAppointments = 0l;

        totalAppointments = appointmentDetailsRepository.count();
        totalBookedAppointments = appointmentDetailsRepository.countOfAppointmentsByState(AppointmentState.BOOKED);
        totalConfirmedAppointments = appointmentDetailsRepository.countOfAppointmentsByState(AppointmentState.CONFIRMED);
        totalCompletedAppointments = appointmentDetailsRepository.countOfAppointmentsByState(AppointmentState.COMPLETED);
        totalCancelledAppointments = appointmentDetailsRepository.countOfAppointmentsByState(AppointmentState.CANCELLED);
        log.debug("................OverAll Summary ..............");
        log.debug("totalAppointments = {}", totalAppointments);

        SummaryForDashboard overallSummaryForDashboard = new SummaryForDashboard(totalAppointments, totalBookedAppointments, totalCancelledAppointments, totalCompletedAppointments, totalConfirmedAppointments);

        totalAppointments = appointmentDetailsRepository.countOfCurrentDateAppointments();
        totalBookedAppointments = appointmentDetailsRepository.countOfCurrentDateAppointmentsByState(AppointmentState.BOOKED.value());
        totalConfirmedAppointments = appointmentDetailsRepository.countOfCurrentDateAppointmentsByState(AppointmentState.CONFIRMED.value());
        totalCompletedAppointments = appointmentDetailsRepository.countOfCurrentDateAppointmentsByState(AppointmentState.COMPLETED.value());
        totalCancelledAppointments = appointmentDetailsRepository.countOfCurrentDateAppointmentsByState(AppointmentState.CANCELLED.value());

        log.debug("....................today's Summary....................");
        log.debug("totalAppointments = {}", totalAppointments);

        SummaryForDashboard todaySummary = new SummaryForDashboard(totalAppointments, totalBookedAppointments, totalCancelledAppointments, totalCompletedAppointments, totalConfirmedAppointments);

        // get prajek pro wallet details

        List<PrajekProWalletDTO> prajekProWalletList = new ArrayList<>();
        List<Currency> currencyList = prajekproWalletDetailsRepository.getDistinctCurrency();
        for (Currency currency : currencyList) {
            Double totalPrajekProWalletAmount = prajekproWalletDetailsRepository.getTotalPrajekProWalletAmount(currency);
            Double totalWalletAppointmentCommissionAmount = prajekproWalletDetailsRepository.getTotalAmountByAmountType(WalletAmountType.APPOINTMENT_COMISSION, currency);
            Double totalWalletSubscriptionCommissionAmount = prajekproWalletDetailsRepository.getTotalAmountByAmountType(WalletAmountType.SUBSCRIPTION_COMISSION, currency);
            if (!CheckUtil.hasValue(totalWalletSubscriptionCommissionAmount)) {
                totalWalletSubscriptionCommissionAmount = 0.0d;
            }
            if (!CheckUtil.hasValue(totalWalletAppointmentCommissionAmount)) {
                totalWalletAppointmentCommissionAmount = 0.0d;
            }
            Double totalWalletCommissionAmount = totalWalletAppointmentCommissionAmount + totalWalletSubscriptionCommissionAmount;
            Double totalWalletEssentialAmount = prajekproWalletDetailsRepository.getTotalAmountByAmountType(WalletAmountType.ESSENTIAL, currency);
            PrajekProWalletDTO prajekProWallet = new PrajekProWalletDTO(totalPrajekProWalletAmount, totalWalletCommissionAmount, totalWalletEssentialAmount);
            prajekProWallet.setCurrencies(currency);
            prajekProWalletList.add(prajekProWallet);
        }

        DashboardInfoDTO dashboardInfo = new DashboardInfoDTO(todaySummary, overallSummaryForDashboard, prajekProWalletList);

        return new BaseWrapper(dashboardInfo);
    }
}
