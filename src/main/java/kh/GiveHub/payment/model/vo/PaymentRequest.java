package kh.GiveHub.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
	private String impUid;
	private String merchantUid;
	private Integer amount;
	private String name;
	private Integer doNo;
	private Integer memNo;
}
