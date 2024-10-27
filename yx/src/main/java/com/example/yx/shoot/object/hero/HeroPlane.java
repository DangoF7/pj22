package com.example.yx.shoot.object.hero;

import com.example.yx.shoot.ShootGame;
import com.example.yx.shoot.object.father.FlyingObject;
import com.example.yx.Tool;
import javafx.scene.image.Image;

public class HeroPlane extends FlyingObject {
    private static Image[] images;
    private int life = 3;
    private int bulletCount;
    private int fire = 2;

    static {
        images = new Image[2];
        images[0] = Tool.readImg("own1.png");
        images[1] = Tool.readImg("ownbz.png");
    }

    public HeroPlane() {
        super(images[0], ShootGame.WIDTH / 2, ShootGame.HEIGHT / 4 * 3);
    }

    public void addLife() { life++; }
    public void subLife() { life--; }
    public int getLife() { return life; }
    public void addFire() { fire++; }
    public void ClearFire() { fire = 0; }

    public HeroBullet[] shootBullet() {
        if (fire >= 3) bulletCount = 3;
        else if (fire == 2) bulletCount = 2;
        else bulletCount = 1;

        HeroBullet[] hbs = new HeroBullet[bulletCount];
        int yStep = (int) (this.image.getHeight() / 2);
        int xStep = (int) (this.image.getWidth() / (bulletCount + 1));
        for (int i = 0; i < hbs.length; i++) {
            hbs[i] = new HeroBullet(this.x - ((int) this.image.getWidth() / 2) + (i + 1) * xStep, this.y - yStep);
        }
        return hbs;
    }

    public void moveLeft() {
        int newX = getX() - 5;
        if (newX >= 0) setX(newX);
    }

    public void moveRight() {
        int newX = getX() + 5;
        if (newX <= ShootGame.WIDTH) setX(newX);
    }

    public void moveUp() {
        int newY = getY() - 5;
        if (newY >= 0) setY(newY);
    }

    public void moveDown() {
        int newY = getY() + 5;
        if (newY <= ShootGame.HEIGHT) setY(newY);
    }

    public void setImage() { setImage(images); }
    int index = 0;
    public void changeImage() {
        setImage(images[1]);
    }

    @Override
    public void setImage(Image[] images) {
        if (state == ALIVE) {
            image = images[0];
        } else if (state == DEAD) {
            image = images[index];
            index++;
            if (index == images.length) state = DELETABLE;
        } else {
            image = null;
        }
    }
}
