package kh.GiveHub.image.controller;

import java.io.File;
import java.util.List;

import kh.GiveHub.common.config.CloudflareR2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kh.GiveHub.donation.model.service.DonationService;
import kh.GiveHub.image.model.service.ImageService;
import kh.GiveHub.news.model.service.NewsService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Controller
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
	private final ImageService iService;
	private final DonationService dService;
	private final NewsService nService;
	private final CloudflareR2Client r2Client;

	@PostMapping("/temp")
	@ResponseBody
	public ResponseEntity<String> saveTemp(
			@RequestParam("image") MultipartFile file,
			@RequestParam("imgType") String imgType,
			@RequestParam("imgName") String imgName) {

		String imageUrl = iService.saveTemp(file, imgName, imgType);
		return ResponseEntity.ok(imageUrl);
	}

	@PostMapping("/upload")
	@ResponseBody
	public boolean saveUpload(
			@RequestParam(value="uploadFiles", required=false) List<String> list,
			@RequestParam("bid") int bid,
			@RequestParam("boardType") String boardType,
			@RequestParam("content") String content) {

		iService.saveUpload(list, bid, boardType);

		int result = 0;

		if(boardType.equals("donation")) {
			result = dService.setContent(bid, content);
		} else {
			result = nService.setContent(bid, content);
		}

		return result > 0;
	}

}
