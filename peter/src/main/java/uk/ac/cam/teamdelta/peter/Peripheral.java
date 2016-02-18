package uk.ac.cam.teamdelta.peter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

class Peripheral {

    private Mat centerMask;
    private Mat centerMaskInvert;

    Peripheral(int rows, int cols, int type){
        centerMask = makeCenterMask(rows,
                                    cols,
                                    type,
                                    false);
        centerMaskInvert = makeCenterMask(rows,
                                          cols,
                                          type,
                                          true);
    }

    private static Mat makeCenterMask(int rows, int cols, int type, boolean invert){
        double maxRadius = Math.sqrt(rows*rows/4 + cols*cols/4);
        Mat centerMask = new Mat(rows, cols, type);
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++){
                double dX = (x - rows / 2);
                double dY = (y - cols / 2);
                double radius = Math.sqrt(dX*dX + dY*dY);
                double value = 1.3 * radius / maxRadius;
                if(invert){
                    value = 1.0 - value;
                }
                double[] pix = centerMask.get(x, y);
                for(int i=0; i<pix.length; i++){
                    pix[i] = value * 255.0;
                }
                centerMask.put(x, y, pix);
            }
        }
        return centerMask;
    }

    Mat overlayPeripheral(Mat bottom, Mat top){
        Mat maskBottom = new Mat();
        Core.multiply(bottom, centerMaskInvert, maskBottom, 1/255.0);

        Mat maskTop = new Mat();
        Core.multiply(top, centerMask, maskTop, 1/255.0);

        Mat result = new Mat();
        Core.add(maskBottom, maskTop, result);

        return result;
    }

    Mat multiplyColor(Mat bottom, Scalar color){
        Mat top = bottom.clone();
        Core.multiply(top, color, top);
        return overlayPeripheral(bottom, top);
    }



}
