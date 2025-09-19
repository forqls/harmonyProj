package kh.GiveHub.donation.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import kh.GiveHub.donation.model.mapper.DonationMapper;
import kh.GiveHub.donation.model.vo.Donation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class DonationService {
    private final DonationMapper mapper;

    public int deleteDona(String no) {
        return mapper.deleteDona(no);
    }

    public ArrayList<Donation> selectDonaList(int i) {
        return mapper.selectDonaList(i);
    }

    public ArrayList<Donation> selectCategory(Map<String, Object> map) {
        return mapper.selectCategory(map);
    }

    public Donation selectDonation(int doNo, Integer id) {
        Donation d = mapper.selectDonation(doNo);
        if (d != null && id != null && d.getMemNo()!=id) {
            int result = mapper.updateCount(doNo);
            if (result > 0) {
                d.setDoViews(d.getDoViews() + 1);
            }
        }
        return d;
    }

    public int setContent(int doNo, String content) {
        StringBuilder newContent = new StringBuilder(content);
        //Pattern pattern = Pattern.compile("<img[^>]+?src=\"([^\"]+)\"[^>]*?>");
        //Pattern pattern = Pattern.compile(
        //		"<img[^>]+?src=\"(?:\\.\\./temp/|\\.\\./\\.\\./temp/|/temp/)([^\"]+)\"[^>]*?>");
        Pattern pattern = Pattern.compile(
                "<img[^>]+?src=\"(?:\\.\\./temp/|\\.\\./\\.\\./temp/|/temp/)([^\"]+?)\"([^>]*?)>");
        Matcher matcher = pattern.matcher(content);

        int offset = 0;

        while(matcher.find()) {
            String filename = matcher.group(1);
            String attributes = matcher.group(2);

            String oldStr = matcher.group(0);
            String newStr = "<img src=\"/upload/" + filename + "\"" + attributes + ">";

//			String newPath = "/upload/"+filename;

//			int startIndex = matcher.start(1) -
//					matcher.group(0).indexOf(matcher.group(1)) + offset;
//	        int endIndex = matcher.start(1) + filename.length() + offset;
            int startIndex = matcher.start() + offset;
            int endIndex = matcher.end() + offset;

            newContent.replace(startIndex, endIndex, newStr);

            offset += newStr.length() - oldStr.length();
        }

        return mapper.setContent(doNo, newContent.toString());
    }

    public int insertDonation(Donation d) {
        return mapper.insertDonation(d);
    }


    public ArrayList<Donation> selectNew() {
        return mapper.selectNew();
    }

    public String getOldContent(int doNo) {
        return mapper.getOldContent(doNo);
    }

    public ArrayList<Donation> selectMostCategoryList(String mostCategory) {
        return mapper.selectMostCategoryList(mostCategory);
    }

    public ArrayList<Donation> selectDeadLineList() {
        return mapper.selectDeadLineList();
    }

    public int updateDonation(Donation d) {
        return mapper.updateDonation(d);
    }
    //쿼리 delete 가 아니라 update status='N'으로 하는거
    public int deleteDonation(int doNo) {
        return mapper.deleteDonation(doNo);
    }
}
