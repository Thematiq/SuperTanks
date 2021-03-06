package app.windows.controllers;

import app.engine.controllers.World;
import app.engine.models.*;
import app.engine.tools.Algebra;
import app.engine.tools.Orientation;
import app.engine.tools.Vector;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Game controller
 * @author Mateusz Praski
 */
public class GameController implements Initializable {
    // UI
    private final Image fullHP = new Image(getClass().getResource("../resources/full_heart.png").toString());
    private final Image emptyHP = new Image(getClass().getResource("../resources/empty_heart.png").toString());
    // Map objects
    private final Image playerTank = new Image(getClass().getResource("../resources/user_tank.png").toString());
    private final Image enemyTank = new Image(getClass().getResource("../resources/enemy_tank.png").toString());
    private final Image bullet = new Image(getClass().getResource("../resources/bullet.png").toString());
    private final Image rock = new Image(getClass().getResource("../resources/rock.png").toString());

    // Grid objects
    private final Image gridImage = new Image(getClass().getResource("../resources/grid_1.png").toString());

    //Particle System
    private final Image[] explosionAnimation = new Image[]{
            new Image(getClass().getResource("../resources/explosion_2.png").toString()),
            new Image(getClass().getResource("../resources/explosion_1.png").toString())
    };

    private final Image[] fireAnimation = new Image[] {
            new Image(getClass().getResource("../resources/boom.png").toString())
    };

    private final int cellSize = 64;
    private World sim;

    private int viewWidth;
    private int viewHeight;
    private boolean endGame = false;

    @FXML
    private Canvas gameCanvas;
    @FXML
    private GridPane mainGrid;

    /**
     * User input handler
     * @param event keyboard event
     */
    public void inputHandler(KeyEvent event) {
        if (!this.endGame) {
            switch (event.getCode()) {
                case LEFT -> this.sim.rotatePlayer(Orientation.getOrient(7));
                case RIGHT -> this.sim.rotatePlayer(Orientation.getOrient(1));
                case DOWN -> this.sim.movePlayerBackward();
                case UP -> this.sim.movePlayerForward();
                case SPACE -> this.sim.shoot();
                default -> System.out.println("Unrecognized command");
            }
            this.draw();
            if (this.sim.getPlayerHP() <= 0) {
                this.endGame();
            }
        } else {
            Stage stage = (Stage) this.gameCanvas.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Game ending handler
     */
    void endGame() {
        this.endGame = true;
        GraphicsContext gc = this.gameCanvas.getGraphicsContext2D();
        gc.setFont(new Font(72));
        gc.fillText("Game Over", this.gameCanvas.getWidth() / 2 - 200, this.gameCanvas.getHeight() / 2);
        gc.fillText("Total Score: " + this.sim.getScore(), this.gameCanvas.getWidth() / 2 - 200, this.gameCanvas.getHeight() / 2 + 75);
    }


    private void listenCanvasResize(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
        this.gameCanvas.setWidth(this.mainGrid.getWidth());
        this.gameCanvas.setHeight(this.mainGrid.getHeight());
        this.prepareUtils();
        this.draw();
    }

    /**
     * Changes map view size based on a canvas size
     */
    private void prepareUtils() {
        this.viewHeight = (int) this.gameCanvas.getHeight() / this.cellSize;
        this.viewWidth  = (int) this.gameCanvas.getWidth() / this.cellSize;
    }

    /**
     * Main drawer
     */
    private void draw() {
        GraphicsContext gc = this.gameCanvas.getGraphicsContext2D();
        Vector playerPos = this.sim.getPlayerPos();
        Vector middleMap = new Vector(this.viewWidth / 2, this.viewHeight / 2);
        Vector bottomLeft = playerPos.add(middleMap.opposite());
        this.drawGrid(gc, playerPos);

        for (int x = 0; x < this.viewWidth; ++x) {
            for (int y = 0; y < this.viewHeight; ++y) {
                Vector currentPos = bottomLeft.add(new Vector(x, y));
                AbstractMapObject amo = this.sim.objectAt(currentPos);
                if (amo != null) {
                    if (amo.getClass() == Schutzengrabenvernichtungspanzerkraftwagen.class) {
                        this.drawObject(gc, this.enemyTank, new Vector(x, y), amo.getOrient());
                    } else if (amo.getClass() == Bullet.class) {
                        this.drawObject(gc, this.bullet, new Vector(x, y), amo.getOrient());
                    } else if (amo.getClass() == Rock.class) {
                        this.drawObject(gc, this.rock, new Vector(x, y), amo.getOrient());
                    }
                }
                Particle uwu = this.sim.particleAt(currentPos);
                if (uwu != null) {
                    this.drawParticle(gc, uwu, new Vector(x, y));
                }
            }
        }

        this.drawObject(gc, this.playerTank, middleMap, this.sim.getPlayerOrient());

        this.drawUI(gc);
    }

    private void drawGrid(GraphicsContext gc, Vector gridSeed) {
        for (int y = 0; y < this.viewHeight; ++y) {
            for (int x = 0; x < this.viewWidth; ++x) {
                gc.drawImage(this.gridImage, this.cellSize * x, this.cellSize * y, this.cellSize, this.cellSize);
            }
        }
    }

    private void drawParticle(GraphicsContext gc, Particle p, Vector pos) {
        Image particleImg = switch(p.type) {
            case EXPLOSION -> this.explosionAnimation[p.getLifespan() - 1];
            case FIRE -> this.fireAnimation[p.getLifespan() - 1];
        };

        this.drawObject(gc, particleImg, pos, p.getOrient());
    }

    private void drawUI(GraphicsContext gc) {
        int y = 10;
        int x = 10;
        int inc_x = 70;
        for (int i = 0; i < this.sim.getPlayerHP(); ++i) {
            gc.drawImage(this.fullHP, x, y, this.cellSize, this.cellSize);
            x += inc_x;
        }
        for (int j = this.sim.getPlayerHP(); j < this.sim.getPlayerMaxHP(); ++j) {
            gc.drawImage(this.emptyHP, x, y, this.cellSize, this.cellSize);
            x += inc_x;
        }
        gc.setFont(new Font(48));
        gc.fillText("Current score: " + this.sim.getScore(), 10, 120);
    }

    private void drawObject(GraphicsContext gc, Image img, Vector pos, Orientation orient) {
        gc.save();
        gc.rotate(orient.getDegree());

        Vector newPos = Algebra.rotateVector(pos.mult(this.cellSize), orient.getDegree(), new Vector(this.cellSize/2, this.cellSize/2));
        gc.drawImage(img, newPos.x, newPos.y, this.cellSize, this.cellSize);

        gc.restore();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.gameCanvas.setWidth(this.mainGrid.getWidth());
        this.gameCanvas.setHeight(this.mainGrid.getHeight());
        this.mainGrid.widthProperty().addListener(this::listenCanvasResize);
        this.mainGrid.heightProperty().addListener(this::listenCanvasResize);
    }

    public void spawnWorld(int tanks, int rocks, int lives) {
        this.sim = new World(rocks, tanks, lives);
        this.prepareUtils();
        this.draw();
    }
}
