package kh.GiveHub.donation.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.GiveHub.donation.model.exception.DonationException;
import kh.GiveHub.donation.model.service.DonationService;
import kh.GiveHub.donation.model.vo.Donation;
import kh.GiveHub.member.model.exception.MemberException;
import kh.GiveHub.member.model.service.MemberService;
import kh.GiveHub.member.model.vo.Member;
import kh.GiveHub.news.model.service.NewsService;
import kh.GiveHub.news.model.vo.News;
import kh.GiveHub.payment.model.vo.Payment;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class DonationController {

	private final DonationService dService;
	private final NewsService nService;
	private final MemberService mService;
	private final ServletResponse servletResponse;


	@GetMapping("/admin/donaList")
	public String newsList (Model model){
		ArrayList<Donation> list = dService.selectDonaList(0);
		model.addAttribute("list", list);
		return "admin/donaList";
	}

	@GetMapping("/admin/donaDelete/{no}")
	public String deleteDona (@PathVariable("no") String no){
		int result = dService.deleteDona(no);
		if (result > 0) {
			return "redirect:/admin/donaList";
		} else {
			throw new MemberException("실패");
		}
	}

	@GetMapping("payment")
	public String paymentPage (HttpSession session , Model model , @RequestParam("doNo") String doNo) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		model.addAttribute("memName",loginUser.getMemName());
		model.addAttribute("doNo",doNo);
		return "page/paymentPage";
	}

	@GetMapping("/donation/donationWrite")
	public String donationWrite () {
		return "donation/donationWrite";
	}


	@GetMapping("/donation/donationlist")
	public String donationList(Model model) {
		return "donation/donationlist";
	}

	@GetMapping("/category")
	@ResponseBody
	public String category( // 반환 타입을 String으로 변경하여 JSON 오류 해결
							@RequestParam("categorySelect") String categorySelect,
							@RequestParam("searchItem") String searchItem,
							@RequestParam("searchInput") String searchInput,
							@RequestParam("optionSelect") String optionSelect,
							HttpServletResponse response) { // HttpServletResponse 매개변수 사용

		Map<String, Object> map = new HashMap<>();
		map.put("categorySelect", categorySelect);
		map.put("optionSelect", optionSelect);

		Donation d = new Donation();
		if (!searchInput.equals("")) {
			if (searchItem.equals("doTitle")) {
				d.setDoTitle(searchInput);
			} else {
				d.setMemName(searchInput);
			}
		}
		map.put("d", d);

		ArrayList<Donation> list = dService.selectCategory(map);

		JSONArray array = new JSONArray();
		final String R2_PUBLIC_URL_PREFIX = "https://pub-d307c9789e8a4ec2b24b351bfb46478e.r2.dev/";

		for (Donation item : list) {
			JSONObject json = new JSONObject();
			json.put("doCategory", item.getDoCategory());
			json.put("doTitle", item.getDoTitle());
			json.put("doNo", item.getDoNo());
			json.put("doCreateDate", item.getDoCreateDate());
			json.put("doEndDate", item.getDoEndDate());
			json.put("memName", item.getMemName());
			json.put("doViews", item.getDoViews());

			String path = item.getThumbnailPath();
			if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
				json.put("thumbnailPath", R2_PUBLIC_URL_PREFIX + path);
			} else {
				json.put("thumbnailPath", JSONObject.NULL);
			}

			array.put(json);
		}

		response.setContentType("application/json; charset=UTF-8");
		return array.toString();
	}

	@PostMapping("/donation/insert")
	@ResponseBody
	public ResponseEntity<Integer> insertDonation(@ModelAttribute Donation d,
												  HttpSession session) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		d.setMemNo(loginUser.getMemNo());
		d.setMemName(loginUser.getMemName());
		int result = dService.insertDonation(d);
		if (result>0) {
			return ResponseEntity.ok(d.getDoNo());
		}else {
			throw new DonationException("failed : insert donation");
		}
	}

	@PostMapping("/donation/update")
	@ResponseBody
	public ResponseEntity<Integer> updateDonation(@ModelAttribute Donation d,
												  HttpSession session) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		int result = 0;
		if (loginUser != null && loginUser.getMemNo() == d.getMemNo()) {
			result = dService.updateDonation(d);
			if (result == 1) {
				return ResponseEntity.ok(result);
			}else {
				throw new DonationException("failed : update donation - result != 1");
			}
		}else {
			throw new DonationException("failed : update donation - memNo != loginUser");
		}
	}

	//기부페이지 상세보기
	@GetMapping("/donation/donationdetail/{doNo}")
	public ModelAndView selectDona(@PathVariable("doNo") int doNo,HttpSession session, ModelAndView mv , Model model) {
		// 글 상세조회 + 조회수 수정(내가 내 글 조회 or 비회원 조회 -> 조회수 올라가지 않음)
		ArrayList<News> list = nService.nnewsList(doNo);
		model.addAttribute("list", list);

		Member loginUser = (Member)session.getAttribute("loginUser");
		Integer id = null;
		if(loginUser != null) {
			id = loginUser.getMemNo();
		}

		//doNo, memId 를 서비스에 넘겨서 글쓴이 비교 로직 작성
		Donation d = dService.selectDonation(doNo, id);
		long dates = d.getDoEndDate().getTime() - Date.valueOf(LocalDate.now()).getTime();
		long date = dates / (1000 * 60 * 60 * 24);
		//게시글이 존재하면, 게시글 데이터(b)를 donationdetail.html로 전달
		//게시글이 존재하지 않으면 사용자 정의 예외 발생
		if(d != null) {
			mv.addObject("d", d).addObject("date", date).setViewName("donation/donationdetail");

			return mv;
		}else {
			throw new MemberException("게시글 상세보기를 실패하셨습니다.");
		}


	}

	@GetMapping("/donation/edit/{doNo}")
	public String toEdit(@PathVariable("doNo") int doNo,
						 HttpSession session, Model model) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		Integer id = null;
		if(loginUser != null) {
			id = loginUser.getMemNo();
		}
		Donation d = dService.selectDonation(doNo, id);
		model.addAttribute("d",d);
		return "donation/donationEdit";
	}



	@GetMapping("/donation/new")
	@ResponseBody
	public String selectNew(HttpServletResponse response) {
		ArrayList<Donation> list = dService.selectNew();
		JSONArray array = new JSONArray();

		for (Donation d : list) {
			JSONObject json = new JSONObject();
			json.put("doCategory", d.getDoCategory());
			json.put("doTitle", d.getDoTitle());
			json.put("doNo", d.getDoNo()); //
			json.put("thumbnailPath", d.getThumbnailPath());

			array.put(json);
		}

		response.setContentType("application/json; charset=UTF-8");
		return array.toString();
	}


	@GetMapping("/news/newsDetail/{newsNo}")
	public String selectNews(@PathVariable("newsNo") String newsNo, Model model) {
		News news = nService.selectNewsDetail(newsNo);
		model.addAttribute("n", news);

		return "news/newsDetail";
	}


	@GetMapping("/donation/customNews")
	public ResponseEntity<List<Donation>> getCustomNews(@RequestParam(value = "userId" , required = false,defaultValue = "0") int userId){
		ArrayList<Payment> list = mService.selectDonationList(userId,0);

		System.out.println(userId);

		//카테고리만 추출해서 배열에 담음
		String[] categories = new String[list.size()];
		for(int i=0;i<categories.length;i++){
			categories[i] = list.get(i).getDoCategory();
		}

		//기부된 횟수별로 카테고리 횟수 세기
		Map<String,Integer> categoryCount = new HashMap<>();
		for(String category : categories){
			categoryCount.put(category,categoryCount.getOrDefault(category,0)+1);
		}

		//가장 많이 기부된 카테고리 찾기
		String mostCategory = null;
		int maxCount = 0;
		for(Map.Entry<String,Integer> entry : categoryCount.entrySet()){
			if(entry.getValue() > maxCount){
				mostCategory = entry.getKey();
				maxCount = entry.getValue();
			}
		}
		ArrayList<Donation> donations = null;
		if(mostCategory != null) {
			donations = dService.selectMostCategoryList(mostCategory);
		}else{
			donations = dService.selectDeadLineList();
		}

		for(int i = 0; i<donations.size();i++) {
			switch(donations.get(i).getDoCategory()) {
				case "handicap" : donations.get(i).setDoCategory("장애인");break;
				case "child" :donations.get(i).setDoCategory("아동/청소년");break;
				case "old" : donations.get(i).setDoCategory("어르신");break;
				case "family":donations.get(i).setDoCategory("가족");break;
				case "animal":donations.get(i).setDoCategory("동물");break;
				case "environment":donations.get(i).setDoCategory("환경");break;
				case "etc":donations.get(i).setDoCategory("기타");break;
			}
		}


		return ResponseEntity.ok(donations);

	}












}