package uk.ac.cam.teamdelta.peter;


import java.awt.image.BufferedImage;


import org.opencv.core.*;
import org.opencv.imgproc.*;
import java.awt.image.*;



import java.io.*;
import javax.imageio.*;

import java.util.Random;

class Glarer {

    private static final int DEFAULT_RADIUS = 150;
    private static final int OFFSET_X = 100;
    private static final int OFFSET_Y = 50;
    private static final int CENTER_X = OFFSET_X + Utils.FRONT_COLS / 2;
    private static final int CENTER_Y = OFFSET_Y + Utils.FRONT_ROWS / 2;
    private static final int RANGE_X = 30;
    private static final int RANGE_Y = 20;
    private static final int WIDTH_APART = 90;

    private Mat headlight;
    private Random r;

    Glarer(){
        try{
            BufferedImage bi = ImageIO.read(Glarer.class.getResourceAsStream("/headlight.jpg"));

            Mat origMat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            origMat.put(0, 0, data);

            headlight = new Mat();
            Imgproc.resize(origMat, headlight, new Size(DEFAULT_RADIUS * 2, DEFAULT_RADIUS * 2));
        }catch(Exception e){
            e.printStackTrace();
        }

        r = new Random();
    }

    protected void addCar(Mat img){
        int dX = r.nextInt(RANGE_X) - RANGE_X / 2;
        int dY = r.nextInt(RANGE_Y) - RANGE_Y / 2;

        addHeadlight(img, CENTER_X + dX - WIDTH_APART / 2, CENTER_Y + dY);
        addHeadlight(img, CENTER_X + dX + WIDTH_APART / 2, CENTER_Y + dY);
    }

    protected void addHeadlight(Mat img, int x, int y){
        x -= headlight.cols() / 2;
        y -= headlight.rows() / 2;
        Mat target = img.submat(y, y + headlight.rows(), x, x + headlight.cols());
        Core.add(target, headlight, target);
        //System.out.println(headlight.get(100, 100)[0]);
    }

}
