package com.example.yx.shoot.object.enemy;


import com.example.yx.shoot.dao.Award;
import com.example.yx.Tool;
import javafx.scene.image.Image;

import java.util.Random;

/**
 * 大敌机
 */
public class BigPlane extends EnemyPlane implements Award {
    private static Image[] images;
    private int award = new Random().nextInt(2);

    static {
        images = new Image[2];
        images[0] = Tool.readImg("ep05.png");
        images[1] = Tool.readImg("dfjbz.png");
    }

    public BigPlane() {
        super(images[0]);
        life = 2;
        bulletCount = 2;
        ySpeed = 2;
    }

    public void setImage() {
        setImage(images);
    }

    @Override
    public int getAward() {
        return award;
    }
}
