package kh.GiveHub.payment.controller;

import java.util.HashMap;

import kh.GiveHub.payment.model.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpSession;
import kh.GiveHub.member.model.exception.MemberException;
import kh.GiveHub.member.model.service.MemberService;
import kh.GiveHub.member.model.vo.Member;
//import kh.GiveHub.payment.model.service.PaymentService;
import kh.GiveHub.payment.model.vo.Payment;
import kh.GiveHub.payment.model.vo.PaymentRequest;
//import kh.GiveHub.payment.model.vo.PaymentRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService pService;
    private final MemberService mService;

    @PostMapping("/payment/complete")
    public ResponseEntity<String> paymentComplete(@RequestBody PaymentRequest request,HttpSession session) {
        // 결제 완료 후 처리
        Payment payment = new Payment();

        int amount = request.getAmount();
        String rank = null;
        System.out.println(request.getAmount());

        HashMap<String,Object> rankMap = new HashMap<>();
        int totalAmount = pService.totalAmount(request.getMemNo());

        totalAmount += amount;

        if(totalAmount>1000000) {
            rank = "GOLD";
        }else if(totalAmount>100000) {
            rank="SILVER";
        }else if(totalAmount>0) {
            rank="BRONZE";
        }


        rankMap.put("memNo",request.getMemNo());
        rankMap.put("rank",rank);

        int rankResult = mService.updateRank(rankMap);

        System.out.println("랭크리절트 결과 : " + rankResult);

        payment.setPayAmount(request.getAmount());
        payment.setMemName(request.getName());
        payment.setDoNo(request.getDoNo());
        payment.setMemNo(request.getMemNo());
        payment.setSuccess(true);

        int result = pService.savePayment(request);

        System.out.println(result);

        if (result > 0) {
            Member loginUser = (Member) session.getAttribute("loginUser");
            if (loginUser != null) {
                loginUser.setMemGrade(rank); // DB에 업데이트된 등급을 세션에 덧붙여 갱신
                session.setAttribute("currentUser", loginUser); // 세션에 갱신된 유저 정보 저장
            }

            return ResponseEntity.ok("결제 완료");
        } else {
            throw new MemberException("결제 실패");
        }
    }
}
