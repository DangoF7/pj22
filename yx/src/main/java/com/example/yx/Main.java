package com.example.yx;

import com.example.yx.shoot.ShootGame;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 653;
    private static Image background = Tool.readImg("main2.jpg");

    @Override
    public void start(Stage primaryStage) throws Exception {


        ShootGame game = new ShootGame();
        game.start();
        game.setCursor(Cursor.HAND);
        Scene scene = new Scene(game, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("飞机大战");
        primaryStage.getIcons().add(Tool.readImg("own1.png"));
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            game.close();
        });
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("An uncaught exception occurred: " + throwable.getMessage());
        });
        launch(args);
    }
}