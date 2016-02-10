package uk.ac.cam.teamdelta.peter;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;
import java.awt.image.*;

class Night {

    static final Scalar NIGHT_COL = new Scalar(96.0, 58.0, 43.0);

    static void nightTime(Mat source, double intensity){
        Core.multiply(source, NIGHT_COL, source, 1 / (255.0 * intensity));
    }

}
