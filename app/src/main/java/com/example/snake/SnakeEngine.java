package com.example.snake;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class SnakeEngine extends SurfaceView implements Runnable {


    private Thread thread = null;


    private Context context;

    public enum Heading {UP, RIGHT, DOWN, LEFT}

    private Heading heading = Heading.RIGHT;


    private int screenX;
    private int screenY;


    private int snakeLength;


    private int dinerX;
    private int dinerY;


    private int blockSize;


    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    private long nextFrameTime;

    private long FPS = 2;

    private final long MILLIS_PER_SECOND = 1000;

    private int score;


    private int[] snakeXs;
    private int[] snakeYs;

    private volatile boolean isPlaying;

    private Canvas canvas;

    private SurfaceHolder surfaceHolder;

    private Paint paint;

    public SnakeEngine(Context context, Point size) {
        super(context);

        context = context;

        screenX = size.x;
        screenY = size.y;


        blockSize = screenX / NUM_BLOCKS_WIDE;

        numBlocksHigh = screenY / blockSize;


        surfaceHolder = getHolder();
        paint = new Paint();

        snakeXs = new int[200];
        snakeYs = new int[200];

        newGame();
    }

    @Override
    public void run() {

        while (isPlaying) {

            if(updateRequired()) {
                update();
                draw();
            }

        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {

        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;


        spawndiner();


        score = 0;
        FPS = 10;

        nextFrameTime = System.currentTimeMillis();
    }

    public void spawndiner() {
        Random random = new Random();
        dinerX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        dinerY = random.nextInt(numBlocksHigh - 1) + 1;
    }

    private void eatdiner(){

        snakeLength++;

        spawndiner();

        score = score + 1;
        FPS = FPS +1;

    }

    private void moveSnake(){

        for (int i = snakeLength; i > 0; i--) {

            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];


        }


        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){

        boolean dead = false;


        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == numBlocksHigh) dead = true;

        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void update() {

        if (snakeXs[0] == dinerX && snakeYs[0] == dinerY) {
            eatdiner();
        }

        moveSnake();

        if (detectDeath()) {
            newGame();
        }
    }

    public void draw() {

        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 160, 200, 30));

            paint.setColor(Color.argb(255, 40, 26, 13));


            paint.setTextSize(50);
            canvas.drawText("Punkty:" + score, 400, 70, paint);


            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }


            paint.setColor(Color.argb(255, 255, 0, 0));


            canvas.drawRect(dinerX * blockSize,
                    (dinerY * blockSize),
                    (dinerX * blockSize) + blockSize, (dinerY * blockSize) + blockSize, paint);


            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {


        if(nextFrameTime <= System.currentTimeMillis()){

            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(heading){
                        case UP:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.UP;
                            break;
                    }
                } else {
                    switch(heading){
                        case UP:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.UP;
                            break;
                    }
                }
        }
        return true;
    }
}
