package kh.GiveHub.image.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import kh.GiveHub.image.model.vo.Image;

@Mapper
public interface ImageMapper {

	void insertImage(Image img);

	int deleteImage(String filename);

}
