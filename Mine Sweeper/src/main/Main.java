package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    static Field field;
    static boolean fieldExists = false;
    public static final int fieldX = 32;
    public static final int fieldY = 32;
    public static final int fieldW = 80;
    public static final int fieldH = 40;
    public static final int numMines = 800;

    public static void main(String[] args) {
        field = new Field(fieldX, fieldY, fieldW, fieldH, numMines, 0, 0);
        new Thread(Main::launch).start();
    }

    @Override
    public void start(Stage S) throws Exception {
        S.setTitle("Minesweeper");
        Group root = new Group();
        Scene s = new Scene(root);
        S.setScene(s);
        final Canvas c = new Canvas(1000, 700);

        GraphicsContext g = c.getGraphicsContext2D();
        field.Paint(g);
        EventHandler<? super MouseEvent> onClick = (e) -> {
            int x = (int) e.getX();
            int y = (int) e.getY();
            if(x > fieldX && y > fieldY && x < fieldX + fieldW * 16 && y < fieldY + fieldH * 16) {
            if(!fieldExists) {
                field = new Field(fieldX, fieldY, fieldW, fieldH, numMines, (x-fieldX)/16, (y-fieldY)/16);
                fieldExists = true;
            }
                if(field.lost()) {
                    field = new Field(fieldX, fieldY, fieldW, fieldH, numMines, 0, 0);
                    fieldExists = false;
                    return;
                }
                if(e.getButton() == MouseButton.PRIMARY)
                    field.sweep((x - fieldX) / 16, (y - fieldY) / 16);
                if(e.getButton() == MouseButton.SECONDARY)
                    field.flag((x - fieldX) / 16, (y - fieldY) / 16);
                if(e.getButton() == MouseButton.MIDDLE)
                    field.confusion((x - fieldX) / 16, (y - fieldY) / 16);
            }
        };
        System.out.println(1<<7);
        c.addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
        AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long time){
                S.setTitle("Minesweeper: " + field.getMines() + " left");
                c.setWidth(s.getWidth());
                c.setHeight(s.getHeight());
                g.setFill(new Color(1, 1, 1, 1));
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                field.Paint(g);
            }
        };
        timer.start();
        root.getChildren().add(c);
        S.show();
    }
}