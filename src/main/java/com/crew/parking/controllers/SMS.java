package com.crew.parking.controllers;

import com.crew.parking.payload.response.MessageResponse;
import hms.kite.samples.api.EncodingType;
import hms.kite.samples.api.StatusCodes;
import hms.kite.samples.api.sms.SmsRequestSender;
import hms.kite.samples.api.sms.messages.MoSmsReq;
import hms.kite.samples.api.sms.messages.MtSmsReq;
import hms.kite.samples.api.sms.messages.MtSmsResp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/sms")
public class SMS {
    private final static Logger LOGGER = Logger.getLogger(SMS.class.getName());

    @GetMapping("/")
    public ResponseEntity<?> home(){
        return ResponseEntity.ok(new MessageResponse("Parking Home"));
    }

    @GetMapping("/send")
    public ResponseEntity<?> sendSmsEp() {
        try {
            LOGGER.info("Sms sending");

            SmsRequestSender smsMtSender = new SmsRequestSender(new URL("http://localhost:7000/sms/send"));

            MtSmsReq mtSmsReq;
//            mtSmsReq = createSubmitMultipleSms(moSmsReq);
            mtSmsReq = createSimpleMtSms();

            mtSmsReq.setApplicationId("APP_063722 ");
            mtSmsReq.setPassword("f37cfdeb8b6e8edb2aa62aa15207c89c");
            mtSmsReq.setSourceAddress("parking.lk");// default sender address or aliases
            mtSmsReq.setVersion("1.0");
//            mtSmsReq.setEncoding("0");
//            mtSmsReq.setChargingAmount("5");

            mtSmsReq.setDeliveryStatusRequest("0");

            MtSmsResp mtSmsResp = smsMtSender.sendSmsRequest(mtSmsReq);
            String statusCode = mtSmsResp.getStatusCode();
            String statusDetails = mtSmsResp.getStatusDetail();
            if (StatusCodes.SuccessK.equals(statusCode)) {
                LOGGER.info("MT SMS message successfully sent");
                return ResponseEntity.ok(new MessageResponse("SMS send succesfully"));
            } else {
                LOGGER.info("MT SMS message sending failed with status code [" + statusCode + "] "+statusDetails);
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Unexpected error occurred", e);
        }
        return ResponseEntity.internalServerError().body(new MessageResponse("Person not added. Try again later."));

    }

    @GetMapping("/receive")
    public ResponseEntity<?> receiveSmsEp(){
        return ResponseEntity.badRequest().body(new MessageResponse("Person not found."));
    }

    private MtSmsReq createSimpleMtSms() {
        MtSmsReq mtSmsReq = new MtSmsReq();

        mtSmsReq.setMessage("Your vehicle XXXX_0000 was parked on 15th October 2022 from 15:18:30 to 16:18:45 at University car park. \n Your payment amount is Rs.85/= ");
        List<String> addressList = new ArrayList<String>();
        addressList.add("tel:0779867624");
        mtSmsReq.setDestinationAddresses(addressList);

        return mtSmsReq;
    }
}
