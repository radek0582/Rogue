package com.radek.tutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.R.attr.path;
import static android.provider.Telephony.Mms.Part.FILENAME;
import static android.provider.Telephony.Mms.Part._DATA;
import static com.radek.tutorial.Constants.HEIGHT_Y;
import static com.radek.tutorial.Constants.WIDTH_X;
import static com.radek.tutorial.MainThread.canvas;
import static java.lang.StrictMath.abs;

/**
 * Created by Radek on 2017-11-11.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private Rect r = new Rect();

    private ArrayList<Obstacle> dots;
    private ArrayList<Obstacle> dots2;
    private ArrayList<Obstacle> dots3;
    private int newDotId;
    private int newDotIdBlue;
    private int newDotIdPurple;

    int factorXY1;
    int factorXY2;

    boolean isLandscape;

    //private RectPlayer player;
    private RectPlayer line;
    //private Point playerPoint;
    private Point linePoint;
    //private ObstacleManager obstacleManager;

    int marginLeft = 40*WIDTH_X/1080;
    int marginUp = 200*HEIGHT_Y/1920;
    int devMarginU = 0;
    int devMarginL = 0;

    private boolean gameOver = false;
    private int newLevel = -1;
    //private boolean movingPlayer = false;
    private long gameOverTime;
    private long newLevelTime;
    private long startGameTime;
    private boolean levelCompleted = false;
    private boolean nextLevel = false;

    private boolean levelIsScreen = false;

    private int currentLevelX1 = marginLeft;
    private int currentLevelX2 = marginLeft;
    private int currentLevelX3 = marginLeft;
    private int currentLevelY1 = 410*HEIGHT_Y/1920;
    private int currentLevelY2 = 710*HEIGHT_Y/1920;
    private int currentLevelY3 = 1010*HEIGHT_Y/1920;

    private int currentDotX = marginLeft, currentDotY = 410*HEIGHT_Y/1920;
    private int targetDotX=700*WIDTH_X/1080, targetDotY=600*HEIGHT_Y/1920;
    private int drawnX=marginLeft;
    private int drawnY;
    private boolean drawingEnabled = false;
    private int directionDrawingX = 1;//right
    private int directionDrawingY = 1; // up
    private int actualDotX = marginLeft;
    private int actualDotY = 410*HEIGHT_Y/1920;

    private int currentDotXBlue = marginLeft, currentDotYBlue = 710;
    private int targetDotXBlue=700, targetDotYBlue=600;
    private int drawnXBlue=marginLeft;
    private int drawnYBlue;
    private boolean drawingEnabledBlue = false;
    private int directionDrawingXBlue = 1;
    private int directionDrawingYBlue = 1;
    private int actualDotXBlue = marginLeft;
    private int actualDotYBlue = 710;

    private int currentDotXPurple = marginLeft, currentDotYPurple = 1010;
    private int targetDotXPurple=700, targetDotYPurple=600;
    private int drawnXPurple=marginLeft;
    private int drawnYPurple;
    private boolean drawingEnabledPurple = false;
    private int directionDrawingXPurple = 1;//right
    private int directionDrawingYPurple = 1; // up
    private int actualDotXPurple = marginLeft;
    private int actualDotYPurple = 1010;

    private int GRAYLINE = 1;
    private int BLUELINE = 2;
    private int PURPLELINE = 3;

    protected int selectedLine = 1;

    private RectPlayer lightBorder1;
    private RectPlayer lightBorder2;
    private RectPlayer lightBorder3;
    private RectPlayer lightBorder4;

    private RectPlayer line1;
    private RectPlayer line2;
    private RectPlayer line3;

    private int flickeringPhase = 1;

    protected ArrayList<Screen> obstacles;
    private int direction;

    private String loadedText;

    private long startTime;
    private long initTime;

    protected float score = 100;
    private float previousScore=100;
    protected float levelTime = 100;
    protected int gameLevel = 1;

    int pause = 0;// -1 pause OFF, 0 pause ON without message, 1 pause ON

    protected int [][] borders = new int [1080*WIDTH_X/1080] [1920*HEIGHT_Y/1920];
    protected int [] [] colors = new int [1080*WIDTH_X/1080] [1920*HEIGHT_Y/1920];

    private ArrayList<RectPlayer>scoringAreas;

    private RectPlayer border1;
    private RectPlayer border2;
    private RectPlayer border3;
    private RectPlayer border4;

    protected RectPlayer startLine1;
    protected RectPlayer startLine2;
    protected RectPlayer startLine3;

    protected boolean startedGame = false;
    private int elapsedPeriod = 0;

    int levelSizeX = 100;
    int levelSizeY = 100;

    int player_pos_X = 20;
    int player_pos_Y = 10;

    int [][] tilesLevel = new int [100][100];
    int [][] monsterLevel = new int [100][100];

    //private Screen screen;
    Screen [] tile = new Screen[100];
    Screen [] tileMonster = new Screen[100];
    private Screen tile2;
    private Screen player;
    private Screen directionPad;
    BitmapFactory.Options options = new BitmapFactory.Options();

    private int interlacingFPS = 1;
    private String message = "";

    public static void Save (File file, String [] data){
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);

        }catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i< data.length; i++){
                    fos.write(data[i].getBytes());
                    if(i<data.length-1){
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch(IOException e){e.printStackTrace();}
        }
        finally{
            try{
                fos.close();
            }
            catch(IOException e){e.printStackTrace();}
        }
    }

    public static String [] Load (File file){
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);

        }
        catch (FileNotFoundException e){e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String test;
        int anzahl1=0;

        try{
            while((test=br.readLine()) != null){
                anzahl1++;
            }
        }
        catch(IOException e){e.printStackTrace();}

        try{
            fis.getChannel().position(0);
        }
        catch(IOException e ){e.printStackTrace();}

        String [] array = new String [anzahl1];
        String line;
        int i = 0;
        try{
            while ((line=br.readLine()) != null){
                array[i]=line;
                i++;
            }
        }
        catch(IOException e){e.printStackTrace();}
        return array;
    }

    public void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
       /* final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/YourFolder/"
                        );*/

        File path = Environment.getExternalStorageDirectory();

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }

    public GamePanel(Context context) {
        super(context);
        dots = new ArrayList<>();
        dots2 = new ArrayList<>();
        dots3 = new ArrayList<>();

        writeToFile("dupa123");

        float xy1 = WIDTH_X/HEIGHT_Y;
        float xy2 = 9/16;

        if (xy1 <= xy2)
            isLandscape = false;
        else
            isLandscape = true;


        if (isLandscape == false) {
            factorXY1 = WIDTH_X;
            factorXY2 = 1080;
            devMarginU = (HEIGHT_Y - 1920*factorXY1/factorXY2)/2;
        }
        else {
            factorXY1 = HEIGHT_Y;
            factorXY2 = 1920;
            devMarginL = (WIDTH_X - 1080*factorXY1/factorXY2)/2;
        }

/*
        File sdcard = Environment.getExternalStoragePublicDirectory(
                //Environment.DIRECTORY_PICTURES
                Environment.DIRECTORY_DCIM + "/YourFolder/"
        );*/

        File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
        File file = new File(sdcard,"config.txt");

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        System.out.println("dupa111" + text);
        loadedText = text.toString();

/*        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("dupa1");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/



        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aTutorial";

        File file = new File (path + "/savedFile.txt");
        String [] savedText = {"radek82s","BBBB"};

        Save (file, savedText);


        String [] loadText = Load(file);
        String finalString = "";

        for (int i=0; i<loadText.length;i++){
            finalString += loadText[i];
        }
        System.out.println(finalString);*/

        getHolder().addCallback(this);
        //thread = new MainThread(getHolder(), this);

        //obstacleManager = new ObstacleManager();
        obstacleManager();

        setFocusable(true);
    }

    public void obstacleManager (){
        startedGame = false;
        elapsedPeriod = 0;

        score = 100;
        gameLevel = 0;
        startTime = initTime = System.currentTimeMillis();

        obstacles = new ArrayList<>();

        //a1 = (ImageView)findViewById(R.id.a1);
        //populateObstacles();
        for (int i =0; i <100; i++)
            for (int j = 0; j<100; j++) {
                //tilesLevel[i][j] = 1;      // '.'
                monsterLevel[i][j] = -15;      // no monster
            }

        //tilesLevel[player_pos_X+2][player_pos_Y-2] = 2;

        //monsterLevel [25][25] = 1;      // rat
        options.inScaled = false;
        player = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.player, options), 1, 1, 100, 100, 1);

        newLevelCreate();

        for (int x = 0; x < 1080*WIDTH_X/1080; x++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                borders[x][y] = 0;

                if (x >= marginLeft+1000*WIDTH_X/1080 || y >= marginUp+1000*HEIGHT_Y/1920 || x <= marginLeft || y <= marginUp)
                    borders[x][y] =1;
            }

/*        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y ++) {
                tile [x][y] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile01, options), (40 + x * 100) * WIDTH_X / 1080, (200 + y * 100) * HEIGHT_Y / 1920, 100, 100, 1);
            }*/
        tile [1] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile01, options), (40 + 100) * factorXY1/factorXY2, (200 + 100) * factorXY1/factorXY2, 100, 100, 1);
        tile [2] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile02, options), (40 + 100) * factorXY1/factorXY2, (200 + 100) * factorXY1/factorXY2, 100, 100, 1);
        tile [3] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile03, options), (40 + 100) * factorXY1/factorXY2, (200 + 100) * factorXY1/factorXY2, 100, 100, 2);
        tile [4] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile01, options), (40 + 100) * factorXY1/factorXY2, (200 + 100) * factorXY1/factorXY2, 100, 100, 1);

        tileMonster [1] = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.tile001, options), (40 + 100) * factorXY1/factorXY2, (200 + 100) * factorXY1/factorXY2, 100, 100, 1);

        directionPad = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.direct, options), devMarginL+(240)*factorXY1/factorXY2, devMarginU+(1300)*factorXY1/factorXY2, 600, 600, 1);
    }

    public void reset (){
        int limit;
        selectedLine = 1;
        pause = 0;
        startedGame = false;

        newDotId = 0;
        newDotIdBlue = 0;
        newDotIdPurple = 0;

        drawnX = currentDotX = actualDotX = targetDotX =currentLevelX1;
        drawnXBlue = currentDotXBlue = actualDotXBlue = targetDotXBlue = currentLevelX2;
        drawnXPurple = currentDotXPurple = actualDotXPurple =targetDotXPurple= currentLevelX3;
        currentDotY = actualDotY = targetDotY = currentLevelY1;
        currentDotYBlue = actualDotYBlue = targetDotYBlue=currentLevelY2;
        currentDotYPurple = actualDotYPurple = targetDotYPurple=currentLevelY3;

        limit = dots.size();
        for (int i = 0; i < limit; i ++) {
            dots.remove(0);
        }

        limit = dots2.size();
        for (int i = 0; i < limit; i ++) {
            dots2.remove(0);
        }

        limit = dots3.size();
        for (int i = 0; i < limit; i ++) {
            dots3.remove(0);
        }

        for (int x = 0; x < 1080*WIDTH_X/1080; x++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                borders[x][y] = 0;

                if (x >= marginLeft+1000*WIDTH_X/1080 || y >= marginUp+1000*HEIGHT_Y/1920 || x <= marginLeft*WIDTH_X/1080 || y <= marginUp*HEIGHT_Y/1920)
                    borders[x][y] =1;
            }

        for( int x = 0; x < 1080*WIDTH_X/1080; x ++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                colors[x][y] = 0;
            }

        if (gameOver){
            gameLevel = 0;
            //newLevel = 0;
            newLevelCreate();
            gameOver = false;
            System.out.println("gameOver");
            nextLevel = false;
            score = previousScore = 100;
            newLevelTime = System.currentTimeMillis();
        }else if (newLevel == 0 || !gameOver) {
            newLevelCreate();
        }
    }

    private void newLevelCreate(){
        gameLevel ++;
        scoringAreas = new ArrayList<>();
        levelTime = 400;
        levelIsScreen = false;

        int limit = obstacles.size();
        for (int i = 0; i < limit; i ++){
            obstacles.remove(0);
        }

        if (gameLevel == 1) {
            levelSizeX = 100;
            levelSizeY = 20;

            addLevelLine ("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", 0);
            addLevelLine ("%.............................................=.............................................=......%", 1);
            addLevelLine ("%..=.........................................=.....=...........====......==.................=......%", 2);
            addLevelLine ("%.....=====.====.=...=..=.=..=..............=.........=....................==...............=......%", 3);
            addLevelLine ("%.......==.................................=.............=...................==.............=......%", 4);
            addLevelLine ("%.............=.............=.............=.....=...........=....=.............==..........==......%", 5);
            addLevelLine ("%..=.......==............................=.......=................=..............==.......=.=......%", 6);
            addLevelLine ("%....=======================............=.........=................=...............==....=..=......%", 7);
            addLevelLine ("%............=.........................=...........=................=................==.=...=......%", 8);
            addLevelLine ("%.........=...=.......................=.............=..........=======.................=....=......%", 9);
            addLevelLine ("%..=...........=.....................=...............=.....====.......=...............=.....=......%", 10);
            addLevelLine ("%....======.=======.=========.......=.................=.===............=.............=......=......%", 11);
            addLevelLine ("%.........=......=.................=.................===................=...........=.......=..=...%", 12);
            addLevelLine ("%..,..............=...............=...............===...=................=.........=........=......%", 13);
            addLevelLine ("%..=...............=.............=.............===.......=................==......=.........=......%", 14);
            addLevelLine ("%...====.=====.===========......=...........===...........=.................=....=..........=......%", 15);
            addLevelLine ("%....................=.........=.........===...............=.................=..............=......%", 16);
            addLevelLine ("%.....................=.......=.......===...................=.................=.............=......%", 17);
            addLevelLine ("%..=...=...=...=...=...=...=.=.=...=...=...=...=...=...=...=...=...=...=...=...==..=...=...==..=...%", 18);
            addLevelLine ("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", 19);
        }
    }

    private void addLevelLine (String levelLine, int lineNo){
        int tileCode = -1;

        for (int x=0; x <100; x++) {
            if (levelLine.charAt(x) == '.')
                tileCode = 1;
            else if (levelLine.charAt(x) == '=')
                tileCode = 2;
            else if (levelLine.charAt(x) == ',')
                tileCode = 3;
            else if (levelLine.charAt(x) == '%')
                tileCode = 4;


            tilesLevel[x][lineNo] = tileCode;
        }
    }

    private void instertBall (int height, int colA, int colB, int colC, int startX, int startY, int size, int direction){
        //obstacles.add(new Obstacle(height, Color.rgb(colA,colB,colC), startX+marginLeft, startY+marginUp, size, direction));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        obstacles.add(new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.bally4, options), startX, startY, 20, 20, direction, 4));
    }

    private void addRectangleArea (int startX, int startY, int endX, int endY, int colorCode){
        int r=255, g=255, b=255;

        if (colorCode == 1) {
            r = 190;
            g = 0;
            b = 0;
        }
        else if (colorCode == 2) {
            r = 210;
            g = 0;
            b = 210;
        }
        else if (colorCode == 3) {
            r = 0;
            g = 0;
            b = 203;
        }

        scoringAreas.add(new RectPlayer(new Rect(startX+marginLeft, startY+marginUp, endX+marginLeft, endY+marginUp), Color.rgb(r, g, b)));

        for( int x = 0; x < 1080*WIDTH_X/1080; x ++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                if (x >= startX*WIDTH_X/1080+marginLeft && y >= startY*HEIGHT_Y/1920+marginUp & x <= endX*WIDTH_X/1080+marginLeft && y<= endY*HEIGHT_Y/1920+marginUp)
                        colors [x][y] = colorCode; // 1 = red color code
                }
    }

   @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int widht, int height) {

    }

   @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //thread = new MainThread(getHolder(), this);
       thread = new MainThread(getHolder(), this);
        thread.setRunning(true);

       if (thread.getState() == Thread.State.NEW)
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;

        while (retry && counter<1000) {
            counter ++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int xTouch = (int)event.getX();
        int yTouch = (int)event.getY();
        boolean dotAllowed = false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                    message = "";

                    if (xTouch >= devMarginL + 240 * factorXY1/factorXY2 && yTouch >= devMarginU+ 1300 * factorXY1/factorXY2 && xTouch <= devMarginL + 440 * factorXY1/factorXY2 && yTouch <= devMarginU + 1500 * factorXY1/factorXY2) { // up left move
                        movePlayer (1);
                    } else if (xTouch >= devMarginL + 440 * factorXY1/factorXY2 && yTouch >= devMarginU + 1300 * factorXY1/factorXY2 && xTouch <= devMarginL + 640 * factorXY1/factorXY2 && yTouch <= devMarginU + 1500 * factorXY1/factorXY2) { // up move
                        movePlayer (2);
                    } else if (xTouch >= devMarginL + 640 * factorXY1/factorXY2 && yTouch >= devMarginU + 1300 * factorXY1/factorXY2 && xTouch <= devMarginL + 840 * factorXY1/factorXY2 && yTouch <= devMarginU + 1500 * factorXY1/factorXY2) { // up right move
                        movePlayer(3);
                    } else if (xTouch >= devMarginL + 240 * factorXY1/factorXY2 && yTouch >= devMarginU + 1500 * factorXY1/factorXY2 && xTouch <= devMarginL + 440 * factorXY1/factorXY2 && yTouch <= devMarginU + 1700 * factorXY1/factorXY2) { // left move
                        movePlayer(4);
                    } else if (xTouch >= devMarginL + 640 * factorXY1/factorXY2 && yTouch >= devMarginU + 1500 * factorXY1/factorXY2 && xTouch <= devMarginL + 840 * factorXY1/factorXY2 && yTouch <= devMarginU + 1700 * factorXY1/factorXY2) { // right move
                        movePlayer(5);
                    } else if (xTouch >= devMarginL + 240 * factorXY1/factorXY2 && yTouch >= devMarginU + 1700 * factorXY1/factorXY2 && xTouch <= devMarginL + 440 * factorXY1/factorXY2 && yTouch <= devMarginU + 1900 * factorXY1/factorXY2) { // down left move
                        movePlayer(6);
                    } else if (xTouch >= devMarginL + 440 * factorXY1/factorXY2 && yTouch >= devMarginU + 1700 * factorXY1/factorXY2 && xTouch <= devMarginL + 640 * factorXY1/factorXY2 && yTouch <= devMarginU + 1900 * factorXY1/factorXY2) { // down move
                        movePlayer(7);
                    } else if (xTouch >= devMarginL + 640 * factorXY1/factorXY2 && yTouch >= devMarginU + 1700 * factorXY1/factorXY2 && xTouch <= devMarginL + 840 * factorXY1/factorXY2 && yTouch <= devMarginU + 1900 * factorXY1/factorXY2) { // down right move
                        movePlayer(8);
                    }

                System.out.println ("" + xTouch + " " + yTouch);
                break;
        }
        return true;
    }



    public void movePlayer (int direction){
        if (direction == 1){        // up left
            if (tilesLevel [player_pos_X -1][player_pos_Y-1] != 4) {
                player_pos_X--;
                player_pos_Y--;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 2){        // up
            if (tilesLevel [player_pos_X][player_pos_Y-1] != 4) {
                player_pos_Y--;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 3){        // up right
            if (tilesLevel [player_pos_X +1][player_pos_Y-1] != 4) {
                player_pos_X++;
                player_pos_Y--;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 4){        // left
            if (tilesLevel [player_pos_X -1][player_pos_Y] != 4) {
                player_pos_X--;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 5){        // right
            if (tilesLevel [player_pos_X +1][player_pos_Y] != 4) {
                player_pos_X++;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 6){        // down left
            if (tilesLevel [player_pos_X -1][player_pos_Y+1] != 4) {
                player_pos_X--;
                player_pos_Y++;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 7){        // down
            if (tilesLevel [player_pos_X][player_pos_Y+1] != 4) {
                player_pos_Y++;
            }
            else
                message = "You cannot move here.";
        }
        else if (direction == 8){        // down right
            if (tilesLevel [player_pos_X +1][player_pos_Y+1] != 4) {
                player_pos_X++;
                player_pos_Y++;
            }
            else
                message = "You cannot move here.";
        }
    }

    public void update(){
        if(!gameOver) {
            update2();

//            if (levelIsScreen)
//                screen.update();

            if (levelTime <= 0) {
                if (score - previousScore >= 0 && levelCompleted == false) {
                    newLevel = 0;
                    newLevelTime = System.currentTimeMillis();
                    //System.out.println(newLevelTime);
                    levelCompleted = true;
                    previousScore = score;
                } else if (levelCompleted == false){
                    gameOverTime = System.currentTimeMillis();
                    gameOver = true;
                    startedGame = false;
                }
            }

            if (startedGame) {
                int limit = obstacles.size();

                for (int i=0; i < limit; i++) {

                    int x = obstacles.get(i).x + 10;
                    int y = obstacles.get(i).y + 10;

                    if (newLevel != 0 && pause == -1){
                        int [][] pixels = new int [3][3];
                        int areaRedAmount = 0;
                        int areaBlueAmount = 0;
                        int areaGreenAmount = 0;
                    }

                    if (pause == -1 && levelCompleted == false)
                        levelTime -= 0.2;
                }
            }
        }
        else
            update2();
    }

    public void update2 (){
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        int speed = 2;
        int obstacleX, obstacleY;

        if (startedGame == true && pause == -1) {
            //levelTime -= 0.2;

            for (Screen ob : obstacles) {
                // * elapsedTime);
                ob.update();

                obstacleX = ob.getRectangle().centerX();
                obstacleY = ob.getRectangle().centerY();

            }
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        int colA = 0, colB = 0, colC = 0;

        //if (WIDTH_X != 1080)
        if (isLandscape == false)
            canvas.setDensity(160*factorXY1/factorXY2);
        else
            canvas.setDensity(160*factorXY1/factorXY2);

        if (canvas != null){
            super.draw(canvas);
            canvas.drawColor(Color.rgb(16,16,16));

        }

        if (player_pos_X >= 5 && player_pos_Y >= 5 && player_pos_X <= levelSizeX - 5 && player_pos_Y <= levelSizeY - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [player_pos_X - 5 + x] [player_pos_Y - 5 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [player_pos_X - 5 + x] [player_pos_Y - 5 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL+ (40 + 500)*factorXY1/factorXY2);
            player.setY(devMarginU+ (200 + 500)*factorXY1/factorXY2);
        }
        else if (player_pos_X <= 5 && player_pos_Y <= 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [x] [y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [x] [y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + player_pos_X * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + player_pos_Y * 100)*factorXY1/factorXY2);
        }
        else if (player_pos_X >= 5 && player_pos_X < levelSizeX - 5 && player_pos_Y < 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [player_pos_X - 5 + x] [y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [player_pos_X - 5 + x] [y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + 500)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + player_pos_Y * 100)*factorXY1/factorXY2);
        }
        else if (player_pos_X < 5 && player_pos_Y >= 5 && player_pos_Y < levelSizeY - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [x] [player_pos_Y - 5 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [x] [player_pos_Y - 5 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + player_pos_X * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + 500)*factorXY1/factorXY2);
        }
        else if (player_pos_X < 5 && player_pos_Y >= levelSizeY - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [x] [levelSizeY - 10 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    //tile [tileType].update();
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [x] [levelSizeY - 10 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + player_pos_X * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + (player_pos_Y%10) * 100)*factorXY1/factorXY2);
        }
        else if (player_pos_X >= 5 && player_pos_Y >= levelSizeY - 5 && player_pos_X <= levelSizeX - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [player_pos_X - 5 + x] [levelSizeY - 10 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [player_pos_X - 5 + x] [levelSizeY - 10 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + 500)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + (player_pos_Y%10) * 100)*factorXY1/factorXY2);
        }
        else if (player_pos_X >= levelSizeX - 5 && player_pos_Y < 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [levelSizeX - 10 + x] [y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [levelSizeX - 10 + x] [y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + player_pos_X%10 * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + player_pos_Y * 100)*factorXY1/factorXY2);
        }
        else if (player_pos_X >= levelSizeX - 5 && player_pos_Y >= 5 && player_pos_Y < levelSizeY - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [levelSizeX - 10 + x] [player_pos_Y - 5 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [levelSizeX - 10 + x] [player_pos_Y - 5 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL +(40 + player_pos_X%10 * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + 500)*factorXY1/factorXY2);
        }
        else if (player_pos_X >= levelSizeX - 5 && player_pos_Y >= levelSizeY - 5) {

            for (int x = 0; x < 10; x++)       // tiles drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int tileType = tilesLevel [levelSizeX - 10 + x] [levelSizeY - 10 + y];

                    tile [tileType].setX(devMarginL + (40 + x * 100)*factorXY1/factorXY2);
                    tile [tileType].setY(devMarginU + (200 + y * 100)*factorXY1/factorXY2);
                    tile [tileType].draw(canvas);
                }

            for (int x = 0; x < 10; x++)       // monsters drawing
                for (int y = 0; y < 10; y++) {
                    options.inScaled = false;
                    int monsterType = monsterLevel [levelSizeX - 10 + x] [levelSizeY - 10 + y];

                    if (monsterType != -15) {
                        tileMonster[monsterType].setX(devMarginL + (40 + x * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].setY(devMarginU + (200 + y * 100) * factorXY1/factorXY2);
                        tileMonster[monsterType].draw(canvas);
                    }
                }

            player.setX(devMarginL + (40 + player_pos_X%10 * 100)*factorXY1/factorXY2);
            player.setY(devMarginU + (200 + player_pos_Y%10 * 100)*factorXY1/factorXY2);
        }


    //        options.inScaled = false;
        //player = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.player, options), 500, 500, 100, 100, 1);
        player.draw(canvas);
        directionPad.draw(canvas);

        if (message != ""){
            Paint paint = new Paint();
            paint.setTextSize(50*factorXY1/factorXY2);
            paint.setColor(Color.GRAY);
            canvas.drawText(message, devMarginL + 50*factorXY1/factorXY2, devMarginU + 1250*factorXY1/factorXY2, paint);
        }

/*        if (newLevel == -1) {
            Paint paint = new Paint();
            paint.setTextSize(100);

            if (elapsedPeriod < 5)
                paint.setColor(Color.rgb(250, 32, 32));
            else if (elapsedPeriod < 10)
                paint.setColor(Color.rgb(32, 32, 250));
            else
                paint.setColor(Color.rgb(32, 250, 32));

            elapsedPeriod ++;


            if (elapsedPeriod == 15)
                elapsedPeriod = 0;

            if (!nextLevel) {
                canvas.drawText("Start Game", 300, 700, paint);
                canvas.drawText("Hi Score: " + loadedText , 300, 1000, paint);
            }
            else
                canvas.drawText("Level " + gameLevel, 400, 700, paint);
        }

        if (gameOver) {
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.MAGENTA);
            canvas.drawText("Game Over", 300, 800, paint);
        }*/
    }

    public void draw2 (Canvas canvas){
/*
        border1.draw(canvas);
        border2.draw(canvas);
        border3.draw(canvas);
        border4.draw(canvas);
*/

    }

}