package com.prajekpro.api.service.impl;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.domain.ProReviews;
import com.prajekpro.api.dto.*;
import com.safalyatech.common.dto.SingleValue;
import com.prajekpro.api.enums.AppointmentState;
import com.prajekpro.api.enums.GeneralErrorCodes;
import com.prajekpro.api.repository.AppointmentDetailsRepository;
import com.prajekpro.api.repository.ProReviewsRepository;
import com.prajekpro.api.service.AuthorizationService;
import com.prajekpro.api.service.CustomerService;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.enums.Roles;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.repository.UsersRepository;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private ProReviewsRepository proReviewsRepository;

    @Override
    public BaseWrapper getCustomerList(CustomerSearchRequestBodyDTO request, Pageable pageable) throws ServicesException {

        log.debug("to get customer list ");
       // Sort sort = Sort.by("createdTs").descending();
        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        } else {
            pageable = PageRequest.of(0, 10);
        }
        List<String> rolesToGetList = Arrays.asList(Roles.ROLE_CUSTOMER.name());

        Page<Users> usersListPage = usersRepository.findByRolesIn(rolesToGetList, pageable);
        if (!usersListPage.hasContent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        List<Users> usersList = usersListPage.getContent();
        log.debug("users list is empty = {}", usersList.isEmpty());

        List<PPRegisterVO> finalUsersList = new ArrayList<>();
        for (Users users : usersList)
            finalUsersList.add(new PPRegisterVO(users));

        Pagination page = new Pagination(finalUsersList, usersListPage.getTotalElements(), pageable);
        return new BaseWrapper(finalUsersList, page);
    }

    @Override
    public BaseWrapper getCustomerDeactivated(String userId, SingleValue<Integer> adminActivationStatus) throws ServicesException {
        if (!CheckUtil.hasValue(userId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<Users> usersOptional = usersRepository.findById(userId);
        if (!usersOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        Users user = usersOptional.get();

        if(adminActivationStatus.getValue() == 0){
            user.setActiveStatus(ActiveStatus.INACTIVE.value());
        }else{
            user.setActiveStatus(ActiveStatus.ACTIVE.value());
        }

        /* switch (adminActivationStatus) {
            case APPROVED:
                user.setAdminActivationStatus(AdminActivationStatus.APPROVED);
                user.setActiveStatus(ActiveStatus.ACTIVE.value());
                break;
            case DEACTIVATED:
                user.setAdminActivationStatus(AdminActivationStatus.DEACTIVATED);
                user.setActiveStatus(ActiveStatus.INACTIVE.value());
                break;
        }*/
        usersRepository.save(user);

        return new BaseWrapper(userId);
    }

    @Override
    public BaseWrapper getCustomerAppointmentsHistory(String userId, Pageable pageable) throws ServicesException {


        if (!CheckUtil.hasValue(userId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        // List<Integer> state = Arrays.asList(AppointmentState.BOOKED.value(),AppointmentState.CONFIRMED.value(),AppointmentState.CHECKED_IN.value(),AppointmentState.CHECKED_OUT.value());
        List<AppointmentState> state = Arrays.asList(AppointmentState.CANCELLED, AppointmentState.COMPLETED);
        Page<AppointmentDetails> appointmentDetailsPage = appointmentDetailsRepository.findByBookedBy_UserIdAndStateIn(userId, state, pageable);
        List<CustomerAppointmentDTO> customerAppointmentDTOS = new ArrayList<>();
        List<AppointmentDetails> appointmentDetailsList;
        if (!appointmentDetailsPage.hasContent()) {
            appointmentDetailsList = new ArrayList<>();
        }
        appointmentDetailsList = appointmentDetailsPage.getContent();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(customerAppointmentDTOS);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            customerAppointmentDTOS.add(new CustomerAppointmentDTO(details));
        }

        Pagination pagination = new Pagination(customerAppointmentDTOS, customerAppointmentDTOS.size(), pageable);
        return new BaseWrapper(customerAppointmentDTOS, pagination);
    }

    @Override
    public BaseWrapper getCustomerReview(String userId, Pageable pageable) throws ServicesException {

        if (!CheckUtil.hasValue(userId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("modifiedTs").descending());
        } else {
            pageable = PageRequest.of(0, 10, Sort.by("modifiedTs").descending());
        }

        Page<ProReviews> customerReviewsPage = proReviewsRepository.findByCustomer_UserId(userId, pageable);
        List<ProReviews> customerReviewsList;
        if (!customerReviewsPage.hasContent()) {
            customerReviewsList = new ArrayList<>();
        }
        customerReviewsList = customerReviewsPage.getContent();
        List<ProReviewsDTO> customerReviewsDTOSList = new ArrayList<>();


        for (ProReviews reviews : customerReviewsList) {
            customerReviewsDTOSList.add(new ProReviewsDTO(reviews));
        }

        Pagination pagination = new Pagination(customerReviewsDTOSList, customerReviewsPage.getTotalElements(), pageable);
        return new BaseWrapper(customerReviewsDTOSList, pagination);
    }

    @Override
    public BaseWrapper getCustomerAppointments(String userId, Pageable pageable) throws ServicesException {

        if (!CheckUtil.hasValue(userId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        List<Integer> state = Arrays.asList(AppointmentState.BOOKED.value(), AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(), AppointmentState.CHECKED_OUT.value());
        Page<AppointmentDetails> appointmentDetailsPage = appointmentDetailsRepository.findByBookedByAndState(userId, state, pageable);
        List<AppointmentDetails> appointmentDetailsList;
        if (!appointmentDetailsPage.hasContent()) {
            appointmentDetailsList = new ArrayList<>();
        }
        appointmentDetailsList = appointmentDetailsPage.getContent();

        List<CustomerAppointmentDTO> customerAppointmentList = new ArrayList<>();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(customerAppointmentList);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            customerAppointmentList.add(new CustomerAppointmentDTO(details));
        }
        Pagination pagination = new Pagination(customerAppointmentList, appointmentDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(customerAppointmentList, pagination);
    }

    @Override
    public BaseWrapper getCustomerDetails(String userId) throws ServicesException {
        if (!CheckUtil.hasValue(userId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<Users> usersOptional = usersRepository.findById(userId);
        if (!usersOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        Users customer = usersOptional.get();
        List<AppointmentState> state = Arrays.asList(AppointmentState.BOOKED);
        int bookedAppointments = appointmentDetailsRepository.countOfAppointmentsByBookedBy(userId, state);

        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(customer, bookedAppointments);
        return new BaseWrapper(customerDetails);
    }
}
