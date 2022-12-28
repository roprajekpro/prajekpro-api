package com.prajekpro.api.service.impl;

import com.prajekpro.api.dto.OtpSmsResponse;
import com.prajekpro.api.enums.GeneralErrorCodes;
import com.prajekpro.api.service.MessagingService;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.utility.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Slf4j
@Service
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private Environment env;

    @Override
    public boolean sendMessage(String contactNumber, Object message) throws Exception {

        if (!CheckUtil.hasValue(contactNumber)
                || !CheckUtil.hasValue(message))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        log.info("Recieved contactNumber = {} and message = {}", contactNumber, message);

        InputStreamReader in = null;

        try {
            StringBuilder sb = new StringBuilder(env.getProperty("sms.url.part1"));
            sb.append(URLEncoder.encode(message.toString(), "UTF-8"));
            sb.append(env.getProperty("sms.url.part2"));
            sb.append(URLEncoder.encode(contactNumber, "UTF-8"));

            String finalSMSUrl = sb.toString();
            log.info("finalSMSUrl = {}", finalSMSUrl);
            URL smsURL = new URL(finalSMSUrl);

            log.debug("Sending SMS");
            in = new InputStreamReader(smsURL.openStream());
            in.close();
            log.debug("SMS Sent Successfully");

            return true;
        } catch (Exception e) {
            log.error("Error Sending SMS to contactNumber = {} and message = {}. Error Stack is: {}", contactNumber, message, e);
            return false;
        } finally {
            if (CheckUtil.hasValue(in))
                in.close();
        }

    }

    @Override
    public boolean sendOTP(String otp, String mobileNo) throws ServicesException {

        if (!CheckUtil.hasValue(mobileNo)
                || !CheckUtil.hasValue(otp))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        log.info("Recieved mobileNo = {} and otp = {}", mobileNo, otp);

        boolean isOtpSent = false;

        try {
            String otpApiEndpoint = MessageFormat
                    .format(
                            env.getProperty("otp.url"),
                            otp,
                            mobileNo);

            log.info("Formed otpApiEndpoint = {}", otpApiEndpoint);

            String otpApiResponse = executeRESTCallByPost(otpApiEndpoint, null);
            log.info("otpApiResponse = {}", otpApiResponse);

            JSONObject otpApiResponseJson = new JSONObject(otpApiResponse);
            OtpSmsResponse otpSmsResponse = new OtpSmsResponse();
            otpSmsResponse.setMessage(otpApiResponseJson.getString("message"));
            otpSmsResponse.setType(otpApiResponseJson.getString("type"));
            log.info("otpSmsResponse = {}", otpSmsResponse.toString());

            if (otpSmsResponse.getType().equalsIgnoreCase("success"))
                isOtpSent = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOtpSent;
    }

    private String executeRESTCallByPost(String urlToHit, Object valueToWrite) throws Exception {

        String response = "";
        ObjectMapper mapper = new ObjectMapper();
        HttpPost post = null;

        try {
            post = new HttpPost(urlToHit);

            mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
            mapper.setSerializationInclusion(Inclusion.NON_NULL);

            if (valueToWrite != null) {
                post.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

                StringEntity entity = new StringEntity(mapper.writeValueAsString(valueToWrite));
                post.setEntity(entity);
            }

            HttpClient http = HttpClientBuilder.create().build();
            InputStream stream = http.execute(post).getEntity().getContent();

            int c;
            while ((c = stream.read()) != -1)
                response += ((char) c);
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != post)
                post.releaseConnection();
        }

        return response;
    }
}
