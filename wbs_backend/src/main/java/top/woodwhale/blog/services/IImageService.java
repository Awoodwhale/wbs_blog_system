package top.woodwhale.blog.services;

import org.springframework.web.multipart.MultipartFile;
import top.woodwhale.blog.response.ResponseResult;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file);

    void getImage(String imageUrl);

    ResponseResult listImages(int page, int size);

    ResponseResult deleteImage(String imageId);
}
