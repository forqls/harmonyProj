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

import kh.GiveHub.common.config.WebMvcConfig;
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

	//버킷 이름 주입
	@Value("${cloudflare.r2.temp.bucket}")
	private String r2TempBucket;

	//영구 버킷 이름 주입
	    @Value("${cloudflare.r2.upload.bucket}")
		private String r2UploadBucket;

//	private String basePath = WebMvcConfig.getBasePath();
//	private String tempPath = basePath + "/temp/";
//	private String uploadPath =basePath + "/upload/";

	public String saveTemp(MultipartFile file,
						   String imgName, String imgType) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100);
		String rename = sdf.format(new Date())+ranNum+"_"+imgName;
		if (imgType.equals("0")) {
			rename = "T"+rename;
		}
		byte[] data = null;
		try {
			data = file.getBytes();
		} catch (IOException ex) {
			throw new RuntimeException("Failed to get bytes from image"+ex.getMessage());
		}

		r2Client.uploadImage(r2TempBucket, rename, data);
		String baseUrl = r2PublicUrl;
		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		String imageUrl = baseUrl + "/" + r2TempBucket + "/" + rename;
		return imageUrl;
	}

	public boolean saveUpload(List<String> list, int bid, String boardType) {
		// 로컬 파일 처리 관련 주석은 모두 제거

		for (String imageUrl : list) {
			// DB에 저장된 URL에서 파일명(key)을 추출
			String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

			// R2 클라이언트를 이용해 파일을 임시 버킷 -> 영구 버킷으로 복사/이동
			// CloudflareR2Client.java에 moveImage(key, sourceBucket, destinationBucket) 메서드가 있다고 가정
			r2Client.moveImage(key);

			// DB에 저장할 최종 영구 URL 생성
			String finalImageUrl = r2PublicUrl + "/" + key;

			// DB에 최종 URL과 파일 정보를 저장
			Image img = new Image();
			img.setImgPath(finalImageUrl); // 영구 URL을 저장
			img.setImgName(key.substring(key.lastIndexOf("_") + 1));
			img.setImgRename(key);
			img.setImgType(key.startsWith("T") ? "0" : "1");
			img.setRefNo(bid);
			img.setBoardType(boardType.equals("donation") ? "D" : "N");
			mapper.insertImage(img);
		}
		return true;
	}
//
//	public List<String> compareContent(String content, String oldcontent) {
//		List<String> oldFiles = new ArrayList<String>();
//        List<String> delFiles = new ArrayList<>();
//		//Pattern pattern = Pattern.compile("<img[^>]+?src=\"/upload/([^\"]+)\"[^>]*?>");
//        //Pattern pattern = Pattern.compile("<img[^>]+?src=\"(?:/upload/|../upload/)([^\"]+)\"[^>]*?>");
//        Pattern pattern = Pattern.compile("<"
//        		+ "img[^>]+?src=\"(?:/upload/|\\.\\./upload/|\\.\\./\\.\\./upload/)([^\"]+)\"[^>]*?>");
//        Matcher matcher = pattern.matcher(oldcontent);
//
//        while (matcher.find()) {
//        	String filename = matcher.group(1);
//        	oldFiles.add(filename);
//        }
//
//        for (String oldFile : oldFiles) {
//        	if(!content.contains(oldFile)) {
//        		delFiles.add(oldFile);
//        	}
//        }
//        return delFiles;
//	}
//
//	public boolean deleteImage(List<String> delFiles) {
//	    int totalCount = delFiles.size();
//	    int successCount = 0;
//
//	    for (String filename : delFiles) {
//	        File file = new File(uploadPath + filename);
//	        boolean fileDeleted = false;
//
//	        if (file.exists()) {
//	            try {
//	                fileDeleted = file.delete();
//	                if (fileDeleted) {
//	                    int dbResult = mapper.deleteImage(filename);
//	                    if (dbResult > 0) {
//	                        successCount++;
//	                    }
//	                }
//	            } catch (SecurityException e) {
//	                e.printStackTrace();
//	            }
//	        } else {
//	            int dbResult = mapper.deleteImage(filename);
//	            if (dbResult > 0) {
//	                successCount++;
//	            }
//	        }
//	    }
//	    return successCount == totalCount;
//	}
}
