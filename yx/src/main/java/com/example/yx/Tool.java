package com.example.yx;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * 工具类
 */
public class Tool {
    // 获取图片
    public static Image readImg(String name) {
        return new Image(name);
    }

    // 获取 Mp3
    public static MediaPlayer readMp3(String name) {
        // 使用相对路径从 classpath 加载音频文件
        URL resource = Tool.class.getResource("/com/example/yx/" + name);
        if (resource == null) {
            System.err.println("音频资源路径错误：" + name);
            return null;
        }
        System.out.println("成功加载音频资源：" + resource.toString());
        Media media = new Media(resource.toString());
        return new MediaPlayer(media);
    }
}
