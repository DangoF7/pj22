package com.example.yx.shoot;

import com.example.yx.shoot.ShootGame;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    @Test
    public void testScoreIncrementAfterBulletHit() {
        ShootGame shootGame = new ShootGame();
        // 生成敌人
        shootGame.addEnemy();

        // 模拟玩家子弹击中敌人
        shootGame.HeroShoot();
        shootGame.enemyHitHeroBullet();
        shootGame.removeEnemy();
        shootGame.removeHeroBullet();

        // 检查得分是否正确增加
        assertEquals(10, shootGame.score);
    }

    @Test
    public void testPlayerMovement() {
        ShootGame shootGame = new ShootGame();

        // 模拟按下A键，向左移动
        shootGame.handleKeyRelease(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.A, false, false, false, false));

        // 检查玩家是否向左移动
        assertTrue(shootGame.isMovingLeft);
        assertFalse(shootGame.isMovingRight);

        // 模拟按下D键，向右移动
        shootGame.handleKeyRelease(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.D, false, false, false, false));

        // 检查玩家是否向右移动
        assertFalse(shootGame.isMovingLeft);
        assertTrue(shootGame.isMovingRight);

        // 模拟释放A键和D键，停止移动
        shootGame.handleKeyRelease(new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.A, false, false, false, false));
        shootGame.handleKeyRelease(new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.D, false, false, false, false));

        // 检查玩家是否停止移动
        assertFalse(shootGame.isMovingLeft);
        assertFalse(shootGame.isMovingRight);
    }

}