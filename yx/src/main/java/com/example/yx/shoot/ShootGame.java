package com.example.yx.shoot;

import java.util.logging.*;
import com.example.yx.shoot.dao.Award;
import com.example.yx.shoot.dao.Score;
import com.example.yx.shoot.object.enemy.*;
import com.example.yx.shoot.object.father.FlyingObject;
import com.example.yx.shoot.object.hero.HeroBullet;
import com.example.yx.shoot.object.hero.HeroPlane;
import com.example.yx.Tool;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import java.util.*;

public class ShootGame extends Group {
    private static final Logger logger = Logger.getLogger(ShootGame.class.getName());

    public static final int WIDTH = 400;
    public static final int HEIGHT = 653;
    private static Canvas canvas = new Canvas(ShootGame.WIDTH, ShootGame.HEIGHT);
    private static GraphicsContext gc = canvas.getGraphicsContext2D();
    static Image[] bgs = new Image[1];

    static {
        bgs[0] = Tool.readImg("backgroud2.jpg");
    }

    private static Image background = bgs[0];
    private static Image pause = Tool.readImg("pause.png");
    private static Image gameOver = Tool.readImg("gameover.png");
    private static Image startButton = Tool.readImg("startButton.png");

    private static final int START = 0;
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int OVER = 3;
    private int state = START;
    private List<Enemy> enemys = Collections.synchronizedList(new ArrayList<>());
    private List<HeroBullet> hbs = Collections.synchronizedList(new ArrayList<>());
    private HeroPlane heroPlane = new HeroPlane();
    int y1 = 0;
    int y2 = (int) -background.getHeight();
    int count = 0;
    static Timer timer;
    public int score = 0;

    // 获取音频资源的方式
    static MediaPlayer shot_P = Tool.readMp3("shot.mp3");
    static MediaPlayer over_g = Tool.readMp3("gameover.mp3");
    static MediaPlayer bom = Tool.readMp3("bomb.mp3");
    static MediaPlayer back = Tool.readMp3("background.mp3");

    int size = 20;
    int leve = 150;
    boolean isHitHero;
    boolean introduce = false;

    public ShootGame() {
        getChildren().add(canvas);
        listener();
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);
        canvas.setOnKeyReleased(this::handleKeyRelease);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        // 添加 AnimationTimer 来实现持续移动
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isMovingLeft && !isMovingRight) {
                    heroPlane.moveLeft();
                } else if (isMovingRight && !isMovingLeft) {
                    heroPlane.moveRight();
                }
                if (isMovingUp && !isMovingDown) {
                    heroPlane.moveUp();
                } else if (isMovingDown && !isMovingUp) {
                    heroPlane.moveDown();
                }
            }
        };
        timer.start();
    }

    public boolean isMovingLeft = false;
    public boolean isMovingRight = false;
    public boolean isMovingUp = false;
    public boolean isMovingDown = false;

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case A -> isMovingLeft = true;
            case D -> isMovingRight = true;
            case W -> isMovingUp = true;
            case S -> isMovingDown = true;
            case SPACE -> {
                if (shot_P.getStatus() != MediaPlayer.Status.PLAYING) {
                    shot_P.play();
                    shot_P.seek(Duration.ZERO);
                }
                HeroShoot();
            }
            default -> { }
        }
    }

    public void handleKeyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case A -> isMovingLeft = false;
            case D -> isMovingRight = false;
            case W -> isMovingUp = false;
            case S -> isMovingDown = false;
            default -> { }
        }
    }

    public void start() {
        paintStart();
        for (int i = 0; i < 2; i++) {
            addEnemy();
        }
        enemyShoot();
        background = bgs[0];
        startBackgroundMusic();
    }


    private void startBackgroundMusic() {
        if (state == RUNNING && back!= null) {
            back.setCycleCount(MediaPlayer.INDEFINITE);
            back.play();
        }else {
            System.err.println("背景音乐加载失败");
        }
    }

    //注册页面鼠标监听
    private void listener() {
        canvas.setOnMouseClicked(event -> {
            if (event.getX() >= 110 && event.getX() <= 300 & event.getY() >= 475 && event.getY() <= 537) {
                if (!introduce) {

                    introduce = true;
                } else {
                    state = RUNNING;
                    //timer 为空说明游戏未启动或终止
                    if (timer == null) {
                        heroPlane = new HeroPlane();
                        hbs = new ArrayList<>();
                        enemys = new ArrayList<>();
                        score = 0;
                        y1 = 0;
                        y2 = (int) -background.getHeight();
                        count = 0;
                        timer();
                    }
                }
            } else {
                switch (state) {
                    case RUNNING:
                        if (timer == null) {
                            timer();
                        }
                        break;
                    case START:
                        timer();
                        state = RUNNING;
                        break;
                    case OVER:
                        heroPlane = new HeroPlane();
                        hbs = new ArrayList<>();
                        enemys = new ArrayList<>();
                        score = 0;
                        y1 = 0;
                        y2 = (int) -background.getHeight();
                        count = 0;
                        state = RUNNING;
                        if (timer == null) {
                            timer();
                        }
                        break;
                }
            }
        });

        canvas.setOnMouseEntered(event -> {
            if (state == PAUSE) {
                state = RUNNING;
            }
        });
        canvas.setOnMouseExited(event -> {
            if (state == RUNNING) {
                state = PAUSE;
            }
        });
    }

    //关闭游戏关闭定时器和背景音乐
    public void close() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (back != null)
            back.stop();
    }

    private void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING) {
                    count++;
                    checkScore();    //检测分数设定难度
                    back.setCycleCount(count);
                    if (count % 50 == 0) {  //通过取模控制速度
                        shot_P.play();
                        shot_P.seek(Duration.ZERO);
                    }
                    if (count % leve == 0) {
                        addEnemy(); //生成敌人频率
                    }
                    if (count % 200 == 0) {
                        enemyShoot(); //敌人发射子弹
                    }
                    if (count % 2 == 0) {
                        backgroundMove();//背景移动
                    }
                    HeroBulletMove();//英雄机子弹移动
                    enemyMove();// 敌人移动
                    enemyHitHeroPlane();//检查敌人撞机
                    enemyHitHeroBullet();//检查敌人是否被击中
                    removeEnemy();//删除敌人(调用)
                    removeHeroBullet();        //删除英雄机子弹(调用)
                    gameOverAction();        //检测游戏结束(调用)
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
                        repaint();
                    }
                });
            }
        }, 10, 10);
    }

    private void checkScore() {
        if (score > 50 && score <= 100) {
            leve = 120;
            background = bgs[0];
        } else if (score > 100 && score <= 200) {
            leve = 100;
            background = bgs[0];
        } else if (score > 200 && score <= 300) {
            leve = 80;
            background = bgs[0];
        } else if (score > 500) {
            leve = 60;
            background = bgs[0];
        }
    }

    private void gameOverAction() {
        if (heroPlane.getState() == FlyingObject.DELETABLE) {
            state = OVER;
        }
    }

    public synchronized void removeHeroBullet() {
        Iterator<HeroBullet> it = hbs.iterator();
        while (it.hasNext()) {
            HeroBullet heroBullet = it.next();
            if (heroBullet.getState() == FlyingObject.DELETABLE    //英雄机子弹的状态是可删除的
                    ||
                    heroBullet.overflow()) {    //英雄机子弹越界
                it.remove();
            }
        }
    }

    public synchronized void removeEnemy() {
        Iterator<Enemy> it = enemys.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();
            if (enemy.getState() == FlyingObject.DELETABLE    //敌人的状态是可删除的
                    ||
                    enemy.overFlow()) {     //敌人越界
                it.remove();
            }
        }
    }

    //画状态态
    private void paintState() {
        switch (state) {
            case RUNNING:
                paintScore();
                back.play();
                break;
            case PAUSE:
                gc.drawImage(pause, 0, 0);
                back.stop();
                break;
            case OVER:
                paintOver();
                back.stop();
        }
    }

    private void paintOver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                over_g.play();
                over_g.seek(Duration.ZERO);
            }
        }).start();
        gc.drawImage(gameOver, 0, 0);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        gc.setFill(Color.BLUE);
        gc.fillText("最终得分：" + score, 110, 160);
        gc.fillText("点击重新开始", 110, 420);
        timer.cancel();
        timer = null;
    }

    private void paintStart() {
//        gc.drawImage(start, -40, 0);
        gc.drawImage(startButton, 110, 375);
//        gc.drawImage(rules, 110, 475);
    }

    private void paintScore() {
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 20));
        gc.setFill(Color.WHITE);
        gc.fillText("得分：" + score, 10, 25);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, size));
        gc.setFill(Color.YELLOW);
        gc.fillText("生命：" + heroPlane.getLife(), 10, 55);
    }

    //背景移动
    private void backgroundMove() {
        y1++;
        y2++;
        if (y1 == 0) {
            y2 = (int) -background.getHeight();
        }
        if (y2 == 0) {
            y1 = (int) -background.getHeight();
        }
    }

    private void repaint() {
        paintBackground();
        paintHeroBillet();
        paintHeroPlane();
        paintEnemy();
        paintState();
    }

    //生成敌人
    public synchronized void addEnemy() {
        EnemyPlane enemyPlane;
        int type = new Random().nextInt(6);
        if (type < 3) {
            enemyPlane = new SmallPlane();
        } else if (type < 5) {
            enemyPlane = new BigPlane();
        } else {
            enemyPlane = new BossPlane();
        }
        enemys.add(enemyPlane);
    }

    //画背景
    private void paintBackground() {
        gc.drawImage(background, 0, y1);
        gc.drawImage(background, 0, y2);
    }

    //画敌人
    private synchronized void paintEnemy() {
        for (Enemy enemy : enemys) {
            enemy.setImage();
            Image image = enemy.getImage();
            if (image != null) {
                gc.drawImage(image, enemy.getX() - image.getWidth() / 2, enemy.getY() - image.getHeight() / 2);
            }
        }
    }

    //敌人发射子弹
    private synchronized void enemyShoot() {
        List<EnemyBullet> ebt = new ArrayList<>();
        for (Enemy enemy : enemys) {
            if (enemy instanceof EnemyPlane && enemy.getState() == FlyingObject.ALIVE) {
                EnemyBullet[] enemyBullets = ((EnemyPlane) enemy).shootBullet();
                ebt.addAll(Arrays.asList(enemyBullets));
            }
        }
        enemys.addAll(ebt);
    }

    //敌人移动
    private synchronized void enemyMove() {
        for (Enemy enemy : enemys) {
            if (enemy.getState() == FlyingObject.ALIVE) {
                enemy.move();
            }
        }
    }

    private synchronized void HeroBulletMove() {
        for (HeroBullet heroBullet : hbs) {
            if (heroBullet.getState() == FlyingObject.ALIVE) {
                heroBullet.move();
            }
        }
    }

    //英雄机发射子弹
    public synchronized void HeroShoot() {
        if (heroPlane.getState() == FlyingObject.ALIVE) {
            HeroBullet[] heroBullets = heroPlane.shootBullet();
            synchronized (heroPlane) {
                hbs.addAll(Arrays.asList(heroBullets));
            }
        }
    }

    //画英雄机子弹
    private synchronized void paintHeroBillet() {
        for (HeroBullet heroBullet : hbs) {
            heroBullet.setImage();
            Image image = heroBullet.getImage();
            if (image != null) {
                gc.drawImage(image, heroBullet.getX() - image.getWidth() / 2, heroBullet.getY() - image.getHeight() / 2);
            }
        }
    }

    //画英雄机
    int num = 0;

    private void paintHeroPlane() {
        //击中减血时，切换图片
        if (isHitHero) {
            if (num == 5) {
                isHitHero = false;
                num = 0;
            }
            heroPlane.changeImage();
            num++;
        } else {
            heroPlane.setImage();
        }
        Image image = heroPlane.getImage();
        if (image != null) {
            gc.drawImage(image, heroPlane.getX() - image.getWidth() / 2, heroPlane.getY() - image.getHeight() / 2);
        }
    }

    //敌人碰撞英雄机
    private synchronized void enemyHitHeroPlane() {
        for (Enemy enemy : enemys) {
            if (enemy.getState() == FlyingObject.ALIVE        //敌人是活着的
                    &&
                    heroPlane.getState() == FlyingObject.ALIVE        //英雄机是活着的
                    &&
                    enemy.touchPlane(heroPlane)) {                //撞上了
                heroPlane.subLife();
                heroPlane.ClearFire();
                waringLife();
                if (heroPlane.getLife() <= 0) {
                    heroPlane.setState(FlyingObject.DEAD);
                }
                tackActionForEnemy(enemy);
            }
        }
    }

    private void waringLife() {
        isHitHero = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    size = 40;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    size = 20;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void tackActionForEnemy(Enemy enemy) {
        if (enemy instanceof EnemyPlane) {
            EnemyPlane enemyPlane = (EnemyPlane) enemy;
            enemyPlane.subLife();
            if (enemyPlane.getLife() <= 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bom.play();
                        bom.seek(Duration.ZERO);
                    }
                }).start();
                enemyPlane.setState(FlyingObject.DEAD);
                if (enemyPlane instanceof Score) {
                    Score score = (Score) enemyPlane;
                    this.score += score.getScore();
                }
                if (enemyPlane instanceof Award && heroPlane.getState() == FlyingObject.ALIVE) {
                    Award award = (Award) enemyPlane;
                    int type = award.getAward();
                    switch (type) {
                        case Award.FIRE:
                            heroPlane.addFire();
                            break;
                        case Award.LIFE:
                            heroPlane.addLife();
                    }
                }
            }
        } else {
            enemy.setState(FlyingObject.DEAD);
        }
        logger.log(Level.INFO, "Player score: {0}", score);
    }

    //敌人碰撞英雄机子弹
    public synchronized void enemyHitHeroBullet() {
        for (Enemy enemy : enemys) {
            for (HeroBullet heroBullet : hbs) {
                if (enemy.getState() == FlyingObject.ALIVE        //敌人是活着的
                        &&
                        heroBullet.getState() == FlyingObject.ALIVE        //英雄机子弹是活着的
                        &&
                        enemy.touchBullet(heroBullet)) {                //撞上了
                    heroBullet.setState(FlyingObject.DEAD);
                    tackActionForEnemy(enemy);
                }
            }
        }
    }
}
