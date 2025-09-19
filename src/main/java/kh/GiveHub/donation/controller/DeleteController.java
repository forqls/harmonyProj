package kh.GiveHub.donation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import kh.GiveHub.donation.model.exception.DonationException;
import kh.GiveHub.donation.model.service.DonationService;
import kh.GiveHub.news.model.exception.NewsException;
import kh.GiveHub.news.model.service.NewsService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeleteController {
    private final DonationService dService;
    private final NewsService nService;

    @GetMapping("/donation/delete/{doNo}")
    public String deleteDonation(@PathVariable("doNo") int doNo) {
        int result = dService.deleteDonation(doNo);
        if (result == 1) {
            return "redirect:/donation/donationlist";
        }else {
            throw new DonationException("deleteDonation failed");
        }
    }

    @GetMapping("/news/delete/{newsNo}")
    public String deleteNews(@PathVariable("newsNo") int newsNo) {
        int result = nService.deleteNews2(newsNo);
        if (result == 1) {
            return "redirect:/news/newsList";
        }else {
            throw new NewsException("deleteNews2 failed");
        }
    }
}
