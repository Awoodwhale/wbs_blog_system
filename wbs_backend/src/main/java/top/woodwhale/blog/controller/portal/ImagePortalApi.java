package top.woodwhale.blog.controller.portal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.services.IImageService;

@Slf4j
@RestController
@RequestMapping("/portal/image")
public class ImagePortalApi {

    @Autowired
    private IImageService imageService;

    /**
     * 通过图片url获取图片
     * @param imageUrl 图片url
     */
    @GetMapping("/{imageUrl}")
    public void getImage(@PathVariable("imageUrl") String imageUrl) {
        imageService.getImage(imageUrl);
    }
}
