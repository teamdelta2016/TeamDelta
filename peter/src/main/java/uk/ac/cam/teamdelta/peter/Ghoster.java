package uk.ac.cam.teamdelta.peter;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;
import java.awt.image.*;

class Ghoster {

    static Mat ghostImage(Mat source, double ghostX, double ghostY, double intensity){
        Mat stretch = Utils.stretchImage(source, ghostX, ghostY);
        Mat ghosted = Utils.overlayImage(source, stretch, intensity);
        return ghosted;
    }

}
