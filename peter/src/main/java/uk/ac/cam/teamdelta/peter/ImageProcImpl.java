package uk.ac.cam.teamdelta.peter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;

import java.awt.image.BufferedImage;
import java.io.IOException;

class ImageProcImpl extends ImageProc {

    private Peripheral peripheralFront;

    ImageProcImpl(ImageProcParams params) {
        super(params);

        peripheralFront = new Peripheral(Utils.FRONT_ROWS, Utils.FRONT_COLS, Utils.MAT_TYPE);
    }

    @Override
    public ImageOutputSet process(ImageInputSet input, boolean isJunction) {
        BufferedImage front = processFront(input.getFrontLeft(), input.getFrontRight());
        BufferedImage left = processSide(input.getLeft(), true);
        BufferedImage right = processSide(input.getRight(), false);
        BufferedImage back = processSide(input.getBack(), true);


        return new ImageOutputSet(front, left, right, back);
    }

    private BufferedImage processSide(BufferedImage iI, boolean isLeftSide) {
        if (iI == null) {
            return null;
        }

        Mat i = Utils.bufferedImageToMat(iI);


        if (params.nightTimeFactor > 0) {
            Night.nightTime(i, params.nightTimeFactor);
        }

        if (params.blurValue > 0) {
            Utils.blurImage(i, params.blurValue * 2);
        }


        double x = (1 - params.darkEdgesFactor);
        Core.multiply(i, new Scalar(x, x, x), i);

        try {
            return Utils.matToBufferedImage(i);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage processFront(BufferedImage fLI, BufferedImage fRI) {
        if (fLI == null || fRI == null) {
            return null;
        }

        Mat fL = Utils.bufferedImageToMat(fLI);
        Mat fR = Utils.bufferedImageToMat(fRI);

        // Stitch
        Mat i = Stitcher.stitchImages(fL, fR);

        if (params.nightTimeFactor > 0) {
            Night.nightTime(i, params.nightTimeFactor);
        }

        if (params.blurValue > 0) {
            Utils.blurImage(i, params.blurValue);
        }

        i = Utils.resizeImage(i, Utils.FRONT_ROWS, Utils.FRONT_COLS);

        Mat ghosted = Ghoster.ghostImage(i, params.ghostFactor, params.ghostFactor, 0.5);

        Mat blurred = ghosted.clone();
        Utils.blurImage(blurred, params.blurValue * 3);

        i = peripheralFront.overlayPeripheral(ghosted, blurred);

        if (params.darkEdgesFactor > 0) {
            double x = 1 - params.darkEdgesFactor;
            i = peripheralFront.multiplyColor(i,
                    new Scalar(x, x, x));
        }

        try {
            return Utils.matToBufferedImage(i);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
