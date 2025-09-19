package kh.GiveHub.payment.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Payment {
	private String payNo;
    private String memName; // 기부자 이름
    private int doNo; // 게시글 번호
    private int payAmount; // 결제 금액
    private boolean success; // 결제 성공 여부
    private int memNo; // 기부자 번호
    private String doTitle;
    private Date payCreatedate;
    private String payStatus;
    private String doCategory;
    private String doContent;
    private Date doStartDate;
    private Date doEndDate;
}

