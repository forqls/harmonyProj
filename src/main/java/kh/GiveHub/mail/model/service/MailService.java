package kh.GiveHub.mail.model.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException; // IOException 추가
import java.util.Random;

@Service
public class MailService {

    private final SendGrid sendGrid;
    private static final String senderEmail = "dksggyqls@gmail.com";

    // SendGrid Bean을 주입받는 생성자
    public MailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    // 랜덤으로 숫자 생성
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }


    // 메일 발송
    public String sendSimpleMessage(String sendEmail) throws IOException {
        String number = createNumber(); // 랜덤 인증번호 생성

        // SendGrid API용 Mail 객체 생성
        Email from = new Email(senderEmail); // 발신자 이메일
        String subject = "이메일 인증";
        Email to = new Email(sendEmail); // 수신자 이메일

        // HTML 내용 설정
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>감사합니다.</h3>";
        Content content = new Content("text/html", body);

        Mail mail = new Mail(from, subject, to, content);

        // API 요청 객체 생성
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send"); // SendGrid API 엔드포인트
            request.setBody(mail.build());

            // API 호출
            Response response = sendGrid.api(request);

            // API 응답 코드 확인 (200, 202는 성공)
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                // API에서 에러가 난 경우 (예: API Key 오류, Unverified Sender 오류 등)
                System.err.println("SendGrid API 전송 실패. 응답 코드: " + response.getStatusCode() + ", 응답 본문: " + response.getBody());

                // 사용자에게 보여줄 예외
                throw new IOException("메일 발송 중 SendGrid API 오류가 발생했습니다. 상세 오류를 로그에서 확인하세요.");
            }

            // 성공 로그를 남깁니다.
            System.out.println("SendGrid API 메일 전송 성공. 응답 코드: " + response.getStatusCode());

        } catch (IOException e) {
            // 네트워크 통신 오류 처리
            e.printStackTrace();
            throw new IOException("메일 발송 중 통신 오류가 발생했습니다.", e);
        }

        return number;
    }
}