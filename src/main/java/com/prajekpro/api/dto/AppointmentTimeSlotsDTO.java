package com.prajekpro.api.dto;


import com.prajekpro.api.domain.TimeSlots;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentTimeSlotsDTO {
    private Long serviceId;
    private Long proId;
    private String date;
    private List<CommonFieldsDTO> timeSlots;

    public AppointmentTimeSlotsDTO(List<Long> bookedAppointmentTimeSlotIds, List<TimeSlots> appointmentTimeSlotsList, AppointmentSlotsDTO request, String timeZone, List<Long> proAvailableTimeSlotId) {
        this.serviceId = request.getServiceId();
        this.proId = request.getProId();
        this.date = request.getDate();

        LocalDate appointmentDate = LocalDate.parse(date);
        log.debug("Appointment Date = {}", appointmentDate);

        LocalDate currentDate = LocalDate.now();
        log.debug("today's date = {}", currentDate);

        LocalTime currentTime = LocalTime.now(ZoneId.of(timeZone));
        log.debug("Today time = {}", currentTime);

        this.timeSlots = new ArrayList<>();

        for (TimeSlots appointmentTimeSlot : appointmentTimeSlotsList) {
            CommonFieldsDTO availableTimeSlot = new CommonFieldsDTO(appointmentTimeSlot);

            //check whether pro is available or not
            if (proAvailableTimeSlotId.contains(appointmentTimeSlot.getId())) {
                availableTimeSlot.setEnabled(true);
            } else {
                availableTimeSlot.setEnabled(false);
            }

            // check for the today's date appointment time slots
            if (appointmentDate.isEqual(currentDate)) {

                DateTimeFormatter parser = DateTimeFormatter.ofPattern("h[:mm] a");
                LocalTime slotTime = LocalTime.parse(appointmentTimeSlot.getDisplayValue(), parser);

                if (slotTime.isBefore(currentTime)) {
                    availableTimeSlot.setEnabled(false);
                }
            }

            if (bookedAppointmentTimeSlotIds.contains(appointmentTimeSlot.getId())) {
                availableTimeSlot.setEnabled(false);
            }

            this.timeSlots.add(availableTimeSlot);
        }

    }

    public AppointmentTimeSlotsDTO(List<TimeSlots> appointmentTimeSlotsList, AppointmentSlotsDTO request) {
        this.serviceId = request.getServiceId();
        this.proId = request.getProId();
        this.date = request.getDate();
        this.timeSlots = new ArrayList<>();
        for (TimeSlots appointmentTimeSlot : appointmentTimeSlotsList) {
            CommonFieldsDTO availableTimeSlot = new CommonFieldsDTO(appointmentTimeSlot);
            availableTimeSlot.setEnabled(false);
            this.timeSlots.add(availableTimeSlot);
        }
    }
}
