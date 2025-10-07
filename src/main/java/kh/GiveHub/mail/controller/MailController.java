package kh.GiveHub.mail.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import kh.GiveHub.mail.model.service.MailService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @ResponseBody
    @PostMapping("/emailCheck")
    public String emailCheck(@RequestParam("email") String mail) {
        try {
            return mailService.sendSimpleMessage(mail);

        } catch (IOException e) {
            e.printStackTrace();
            return "EMAIL_FAIL";
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_FAIL";
        }
    }

    @ResponseBody
    @PostMapping("/findIdemailCheck")
    public String emailCheck2(@RequestParam("email") String mail) {
        try {
            return mailService.sendSimpleMessage(mail);
        } catch (IOException e) {
            e.printStackTrace();
            return "EMAIL_FAIL";
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_FAIL";
        }
    }
    
    @ResponseBody
    @PostMapping("/findPwdemailCheck") // ajax로 보낸 email을 받아서 넘기기
    public String emailCheck3(@RequestParam("email") String mail) {
        try {
            return mailService.sendSimpleMessage(mail);
        } catch (IOException e) {
            e.printStackTrace();
            return "EMAIL_FAIL";
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_FAIL";
        }
    }
    
}
