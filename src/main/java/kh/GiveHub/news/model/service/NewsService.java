package kh.GiveHub.news.model.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import kh.GiveHub.news.model.mapper.NewsMapper;
import kh.GiveHub.news.model.vo.News;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsMapper mapper;

    public News selectNews(int newsNo) {
        return mapper.selectNews(newsNo);
    }

    public ArrayList<News> selectNewsList() {
        return mapper.selectNewsList();
    }

    public int deleteNews(String nNo) {
        return mapper.deleteNews(nNo);
    }

    public ArrayList<News> nnewsList(int doNo) {
        return mapper.nnewsList(doNo);
    }

    public int setContent(int newsNo, String content) {
        StringBuilder newContent = new StringBuilder(content);
        //Pattern pattern = Pattern.compile("<img[^>]+?src=\"([^\"]+)\"[^>]*?>");
//        Pattern pattern = Pattern.compile(
//				"<img[^>]+?src=\"(?:\\.\\./temp/|\\.\\./\\.\\./temp/|/temp/)([^\"]+)\"[^>]*?>");
        Pattern pattern = Pattern.compile(
                "<img[^>]+?src=\"(?:\\.\\./temp/|\\.\\./\\.\\./temp/|/temp/)([^\"]+?)\"([^>]*?)>");
        Matcher matcher = pattern.matcher(content);

        int offset = 0;

        while(matcher.find()) {
            String filename = matcher.group(1);
            String attributes = matcher.group(2);

            String oldStr = matcher.group(0);
            String newStr = "<img src=\"/upload/" + filename + "\"" + attributes + ">";

            int startIndex = matcher.start() + offset;
            int endIndex = matcher.end() + offset;

            newContent.replace(startIndex, endIndex, newStr);

            offset += newStr.length() - oldStr.length();
        }

        return mapper.setContent(newsNo, newContent.toString());
    }

    public int insertNews(News n) {
        return mapper.insertNews(n);
    }


    public News newsDetail(int newsNo) {return mapper.newsDetail(newsNo);}

    public ArrayList<News> selectNewsNew() {
        return mapper.selectNewsNew();
    }

    public News selectNewsDetail(String newsNo) {return mapper.selectNewsDetail(newsNo);
    }

    public String getOldContent(int newsNo) {
        return mapper.getOldContent(newsNo);
    }

    public int updateNews(News n) {
        return mapper.updateNews(n);
    }
    //쿼리 delete 가 아니라 update status='N'으로 하는거
    public int deleteNews2(int newsNo) {
        return mapper.deleteNews2(newsNo);
    }
}