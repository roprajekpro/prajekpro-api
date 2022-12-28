package com.prajekpro.api.util;

import com.safalyatech.common.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.*;
import java.time.*;
import java.util.Date;
import java.util.concurrent.*;

@Slf4j
public class DateUtil {

    public static String getTodayYYYYMMDD() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public String milliSecondToDate(long milliSeconds) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
        Date date = new Date(milliSeconds);
        return dateFormat.format(date);
    }

    public String convertDateFormat(String inputDate) throws ParseException {
        SimpleDateFormat sdfmt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfmt2 = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
        Date dDate = null;
        try {
            dDate = sdfmt1.parse(inputDate);
            return sdfmt2.format(dDate);
        } catch (ParseException e) {
            log.error(GlobalConstants.LOG.ERROR, "convertDateFormat(..)", getClass().getName(), e);
            throw e;
        }
    }

    public static long getDiffInDays(long startEpoch, long endEpoch) {
        return TimeUnit.MILLISECONDS.toDays((endEpoch - startEpoch));
    }

    public static long getDiffInHours(long startEpoch, long endEpoch) {
        return TimeUnit.MILLISECONDS.toHours((endEpoch - startEpoch));
    }

    public static void main(String[] args) {
        String apptDateTimeString = MessageFormat.format("{0}T{1}Z", "2021-12-19", "21:00:00");
        long apptDateTimeInMillis = ZonedDateTime.parse(apptDateTimeString).toInstant().toEpochMilli();
        System.out.println(getDiffInHours(System.currentTimeMillis(), apptDateTimeInMillis));
    }
}
