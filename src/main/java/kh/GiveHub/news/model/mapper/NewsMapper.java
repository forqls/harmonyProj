package kh.GiveHub.news.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kh.GiveHub.news.model.vo.News;

@Mapper
public interface NewsMapper {

    News selectNews(int newsNo);

    ArrayList<News> selectNewsList();

    int deleteNews(String nNo);

    ArrayList<News> nnewsList(int doNo);

    int insertNews(News n);

    int setContent(@Param("newsNo")int newsNo,@Param("newContent") String newContent);

    News newsDetail(int newsNo);

    ArrayList<News> selectNewsNew();

    News selectNewsDetail(String newsNo);

    String getOldContent(int newsNo);

    int updateNews(News n);
    //쿼리 delete 가 아니라 update status='N'으로 하는거
    int deleteNews2(int newsNo);
}
