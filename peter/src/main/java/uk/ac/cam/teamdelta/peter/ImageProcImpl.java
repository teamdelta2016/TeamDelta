package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;

import java.awt.image.BufferedImage;
import java.awt.Graphics;


import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;
import java.awt.image.*;



import java.io.*;
import javax.imageio.*;

class ImageProcImpl extends ImageProc {

    private Peripheral peripheralFront;

    ImageProcImpl(ImageProcParams params){
        super(params);

        peripheralFront = new Peripheral(Utils.FRONT_ROWS, Utils.FRONT_COLS, Utils.MAT_TYPE);
    }

    public ImageOutputSet process(ImageInputSet input){
        BufferedImage front = processFront(input.frontLeft, input.frontRight);
        // BufferedImage left = processSide(input.left, true);
        // BufferedImage right = processSide(input.right, false);
        // BufferedImage back = processBack(input.back);


        return new ImageOutputSet(front, null, null, null);
    }

    private BufferedImage processFront(BufferedImage fLI, BufferedImage fRI){
        Mat fL = Utils.bufferedImageToMat(fLI);
        Mat fR = Utils.bufferedImageToMat(fRI);

        // Stitch
        Mat i = Stitcher.stitchImages(fL, fR);

        if(params.nightTimeFactor > 0){
            Night.nightTime(i, params.nightTimeFactor);
        }

        if(params.blurValue > 0){
            Utils.blurImage(i, params.blurValue);
        }

        i = Utils.resizeImage(i, Utils.FRONT_ROWS, Utils.FRONT_COLS);

        Mat ghosted = Ghoster.ghostImage(i, params.ghostFactor, params.ghostFactor, 0.5);

        Mat blurred = ghosted.clone();
        Utils.blurImage(blurred, params.blurValue * 3);

        i = peripheralFront.overlayPeripheral(ghosted, blurred);

        if(params.darkEdgesFactor > 0){
            i = peripheralFront.overlayColor(i,
                                             new Scalar(10, 10, 10),
                                             params.darkEdgesFactor);
        }

        try{
            return Utils.matToBufferedImage(i);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public ImageOutputSet processStub(ImageInputSet input){
        return new ImageOutputSet(copyImage(input.frontLeft),
                                  copyImage(input.left),
                                  copyImage(input.right),
                                  copyImage(input.back));
    }

    private static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

}
