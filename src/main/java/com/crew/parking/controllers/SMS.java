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

    @GetMapping("/send")
    public ResponseEntity<?> sendSmsEp() {
        try {
            LOGGER.info("Sms sending");

            SmsRequestSender smsMtSender = new SmsRequestSender(new URL("http://localhost:7000/sms/send"));

            MtSmsReq mtSmsReq;
//            mtSmsReq = createSubmitMultipleSms(moSmsReq);
            mtSmsReq = createSimpleMtSms();

            mtSmsReq.setApplicationId("APP_999999");
            mtSmsReq.setPassword("password");
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

    public void onReceivedSms(MoSmsReq moSmsReq) {
        try {
            LOGGER.info("Sms Received for generate request : " + moSmsReq);

            SmsRequestSender smsMtSender = new SmsRequestSender(new URL("http://localhost:7000/sms/send"));

            MtSmsReq mtSmsReq;
//            mtSmsReq = createSubmitMultipleSms(moSmsReq);
            mtSmsReq = createSimpleMtSms();

            mtSmsReq.setApplicationId(moSmsReq.getApplicationId());
            mtSmsReq.setPassword("d3d8c7fb7cd87659a6003e50fb1ba042");
            mtSmsReq.setSourceAddress("mykeyword");// default sender address or aliases
            mtSmsReq.setVersion(moSmsReq.getVersion());
//            mtSmsReq.setEncoding("0");
//            mtSmsReq.setChargingAmount("5");

            String deliveryReq = moSmsReq.getDeliveryStatusRequest();
            if (deliveryReq != null) {
                if (deliveryReq.equals("1")) {
                    mtSmsReq.setDeliveryStatusRequest("1");
                }
            } else {
                mtSmsReq.setDeliveryStatusRequest("0");
            }

            MtSmsResp mtSmsResp = smsMtSender.sendSmsRequest(mtSmsReq);
            String statusCode = mtSmsResp.getStatusCode();
            String statusDetails = mtSmsResp.getStatusDetail();
            if (StatusCodes.SuccessK.equals(statusCode)) {
                LOGGER.info("MT SMS message successfully sent");
            } else {
                LOGGER.info("MT SMS message sending failed with status code [" + statusCode + "] "+statusDetails);
            }


        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Unexpected error occurred", e);
        }
    }

    private MtSmsReq createSimpleMtSms() {
        MtSmsReq mtSmsReq = new MtSmsReq();

        mtSmsReq.setMessage("Welcome to my application");
        List<String> addressList = new ArrayList<String>();
        addressList.add("tel:0779867624");
        mtSmsReq.setDestinationAddresses(addressList);

        return mtSmsReq;
    }

    private MtSmsReq createSubmitMultipleSms(MoSmsReq moSmsReq) {
        MtSmsReq mtSmsReq = new MtSmsReq();

        mtSmsReq.setMessage("This message will receive to multiple users");
        List<String> addressList = new ArrayList<String>();

        addressList.add("tel:123456789");
        addressList.add("tel:456789123");

        mtSmsReq.setDestinationAddresses(addressList);

        return mtSmsReq;
    }

    private MtSmsReq createBinarySm(MoSmsReq moSmsReq) {
        MtSmsReq mtSmsReq = new MtSmsReq();
        mtSmsReq.setMessage(
                "3000000002010000481c010000000000001c000000000007e000720000000000" +
                        "3c1001c10000000001e010fe03e00000001f000bff8ff8000000f00007ffdffc" +
                        "00000080000fedffdc000000602a0f7fefce0000001fd41e7ff7ee0000000060" +
                        "1cfff7ee00000007801cfff3fe00000004001cfffbfe00000003051cfffbfe00" +
                        "07f800fa9cfffbfe007800000c1c7ffffe00000000300e7ffffc0000000021ce" +
                        "7ffffc00001ffe1e373ffff80007e000000fbffff8001800000003dffff00000" +
                        "00000001efffe0000000000000ffffc00000000fff007fff80000007f000003f" +
                        "ff000000380000001ffe0000000000000007fc0000000000000001f800000000" +
                        "00000000600000");
        mtSmsReq.setEncoding(EncodingType.BINARY.getCode());
        mtSmsReq.setBinaryHeader("060504158a0000");

        List<String> addressList = new ArrayList<String>();
        addressList.add(moSmsReq.getSourceAddress());
        mtSmsReq.setDestinationAddresses(addressList);

        return mtSmsReq;
    }

    private MtSmsReq createFlashSms(MoSmsReq moSmsReq) {
        MtSmsReq mtSmsReq = new MtSmsReq();

        mtSmsReq.setMessage("This is a flash SM");

        List<String> addressList = new ArrayList<String>();
        addressList.add(moSmsReq.getSourceAddress());
        mtSmsReq.setDestinationAddresses(addressList);

        mtSmsReq.setEncoding(EncodingType.FLASH.getCode());

        return mtSmsReq;
    }
}
