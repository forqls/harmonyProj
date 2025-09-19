package kh.GiveHub.donation.model.vo;

import lombok.*;

import java.sql.Date;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Donation {
    private int doNo;
    private String doTitle;
    private String doContent;
    private int doGoal;
    private String doCategory;
    private Date doStartDate;
    private Date doEndDate;
    private int doViews;
    private Date doCreateDate;
    private String doStatus;
    private int memNo;
    private String memName;
    private String memId;
    private String thumbnailPath;
    private int payAmount;
    private int payCount;
}
