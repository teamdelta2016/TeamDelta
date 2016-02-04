package uk.ac.cam.teamdelta;

import java.awt.image.BufferedImage;

public class ImageInputSet {
    public ImageInputSet(
        BufferedImage frontLeft_,
        BufferedImage frontRight_,
        BufferedImage left_,
        BufferedImage right_,
        BufferedImage back_
    ) {
        frontLeft = frontLeft_;
        frontRight = frontRight_;
        left = left_;
        right = right_;
        back = back_;
    }

    public final BufferedImage frontLeft, frontRight, left, right, back;
}
