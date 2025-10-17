package kh.GiveHub.image.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kh.GiveHub.common.config.CloudflareR2Client;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kh.GiveHub.image.model.exception.ImageException;
import kh.GiveHub.image.model.mapper.ImageMapper;
import kh.GiveHub.image.model.vo.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class ImageService {
	private final ImageMapper mapper;
	private final CloudflareR2Client r2Client;
	@Value("${cloudflare.r2.public.url}")
	private String r2PublicUrl;


	public String saveTemp(MultipartFile file,
						   String imgName, String imgType) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100);
		String rename = sdf.format(new Date())+ranNum+"_"+imgName.replace(" ", "_");

		if (imgType.equals("0")) {
			rename = "thumb_" + rename;
		}

		byte[] data = null;
		try {
			data = file.getBytes();
		} catch (IOException ex) {
			throw new RuntimeException("Failed to get bytes from image"+ex.getMessage());
		}

		r2Client.uploadImage(rename, data);

		return r2Client.getPublicUrl(rename);
	}

	public String saveUpload(List<String> list, int bid, String boardType) {
		String thumbnailFinalUrl = null;

		for (String imageUrl : list) {
			String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

			Image img = new Image();
			img.setImgPath(imageUrl);
			img.setImgName(key.substring(key.lastIndexOf("_") + 1));
			img.setImgRename(key);

			boolean isThumbnail = key.startsWith("thumb_");
			img.setImgType(isThumbnail ? "0" : "1");

			img.setRefNo(bid);
			img.setBoardType(boardType.equals("donation") ? "D" : "N");
			mapper.insertImage(img);

			if (isThumbnail) {
				thumbnailFinalUrl = imageUrl;
			}
		}
		return thumbnailFinalUrl;
	}
}
