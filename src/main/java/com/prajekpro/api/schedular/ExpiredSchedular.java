package com.prajekpro.api.schedular;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.text.*;
import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Component
@Transactional(rollbackFor = Throwable.class)
public class ExpiredSchedular {

    @Autowired
    private UserOtpRepository userOtpRepository;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AppointmentService appointmentService;


    /**
     * CRON job to cancel all appointments whose appt. date has already passed
     */
    @Scheduled(zone = GlobalConstants.StringConstants.ZONE_ID_PHST, cron = "${cron.appointment.autocancel}")
    public void cancelExpiredAppointments() {
        final String METHOD_NM = "cancelExpiredAppointments";

        String apptDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        log.info(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "Cancelling expired appointments which were confirmed but not completed on or before date: " + apptDate);

        //Get List of appointments which are active and in state
        List<Integer> apptStatesToAutoCancel = Arrays.asList(
                AppointmentState.BOOKED.value(), AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(),
                AppointmentState.CHECKED_OUT.value());
        List<AppointmentDetails> appointmentDetails = appointmentDetailsRepository.selectApptForAutoCancellation(apptStatesToAutoCancel, apptDate, ActiveStatus.ACTIVE.value());

        if (hasValue(appointmentDetails)) {
            //Get Auto Cancel Remark from Lookup
            final RemarksDTO autoCancelRemark = new RemarksDTO(LookUpRowIds.AUTO_CANCEL.value(), GlobalConstants.StringConstants.AUTO_CANCEL_REMARKS);

            //Get Default User for scheduler API's
            Optional<Users> defaultUserOptional = usersRepository.findById(GlobalConstants.StringConstants.DEFAULT_USER_EMAIL_ID);
            if (!defaultUserOptional.isPresent()) {
                log.error(GlobalConstants.LOG.ERROR, METHOD_NM, getClass().getName(), "Default User Not Present. Aborting auto cancellation of appointments");
            } else {
                Users defaultUser = defaultUserOptional.get();

                //Auto-Cancel each Appointment
                final AppointmentState apptState = AppointmentState.CANCELLED;
                appointmentDetails.forEach(appt -> {
                    log.debug(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "Auto cancelling appointment with ID - " + appt.getId());
                    try {
                        appointmentService
                                .updateAppointmentState(
                                        appt.getId(), apptState, autoCancelRemark,
                                        appt.getAppointmentRequestedServices().get(0).getId(), defaultUser);
                    } catch (ServicesException e) {
                        log.error(GlobalConstants.LOG.ERROR, METHOD_NM, getClass().getName(), e);
                    } catch (ParseException e) {
                        log.error(GlobalConstants.LOG.ERROR, METHOD_NM, getClass().getName(), e);
                    }
                });
                log.info(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "Cancelled expired appointments which were confirmed but not completed on or before date: " + apptDate);
            }
        } else
            log.info(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "No appointments found which were confirmed but not completed on or before Date: " + apptDate);
    }


    /**
     * CRON job to clear the OTP table for any past dated OTP
     */
    @Scheduled(zone = GlobalConstants.StringConstants.ZONE_ID_PHST, cron = "${cron.otp.attempts.clear}")
    public void clearOtpAttempts() {
        final String METHOD_NM = "clearOtpAttempts";
        log.info(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "Clearing OTP table");

        userOtpRepository.deleteAll();

        log.info(GlobalConstants.LOG.INFO, METHOD_NM, getClass().getName(), "Cleared OTP table");
    }
}
