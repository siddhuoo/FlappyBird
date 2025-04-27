import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import javax.swing.*;

public class FlappyBeard extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }
    //Pipes
    int pipeX =boardWidth;
    int pipeY = 0;
    int pipewidth =64;     //scaled to 1/6
    int pipeHeight = 812;

    class pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        pipe(Image img) {
            this.img =img;
        }
    }


    // game logic
    Bird bird;
    int velocityX = -4;  //move pipes to this left speed(simulates bird moving right)
    int velocityY = 0;  // move bird up and down speed
    float gravity = 1.0f;
   
    ArrayList<pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBeard() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);


        // load images
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./flappybirdbg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./flappybird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./bottompipe.png"))).getImage();

        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<pipe>();


        //place pipes timer
         placePipTimer = new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               placePipes();
            }
         });
         placePipTimer.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this); // 1000/60 = 16.6
        gameLoop.start();
    }
        public void placePipes(){
            //(0-1) * pipeheight/2 -> (0-256)
            // 128
            //0 -128 -(0-256)--> 1/4 pipeHeight -> 3/4 pipeHeight

            int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random() * (pipeHeight/2));
            int openingSpace = boardHeight/4;

            pipe topPipe =new pipe(topPipeImg);
            topPipe.y = randomPipeY;
            pipes.add(topPipe);
            
            pipe bottomPipe = new pipe(bottomPipeImg);
            bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
            pipes.add(bottomPipe);
         }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
    
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++){
            pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height,null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver){
            g.drawString("Game Over:" + String.valueOf((int) score), 10, 35);

        }
        else{
        g.drawString(String.valueOf((int)score),10, 35);
        }

    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        
        //pipes
        for (int i = 0; i < pipes.size(); i++){
            pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            
            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;  // 0.5 becouse there are 2pipes ! so 0.5*2 = 1,1 for each set of pipe 
            }

            if (collision(bird,pipe)){
            gameOver = true;
            }
        }

        if (bird.y > boardHeight ){
            gameOver = true;
        }
    }

    public boolean collision(Bird a, pipe b){
        return  a.x < b.x + b.width &&  //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&  //a's top right corner passes d's top left corner
                a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;   //a's bottom left corner passes b's top left corner

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipTimer.stop();
            gameLoop.stop();
        }
    }

     @Override
    public void keyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        velocityY = -9;
        if (gameOver){
            // restart the game by resetting the conditions
            bird.y =birdY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipTimer.start();
        }
       }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
