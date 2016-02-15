package uk.ac.cam.teamdelta;

import java.awt.image.BufferedImage;

public class ImageInputSet {
    private final BufferedImage left, frontLeft, frontRight, right, back;

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

    public BufferedImage getLeft() {
        return left;
    }

    public BufferedImage getFrontLeft() {
        return frontLeft;
    }

    public BufferedImage getFrontRight() {
        return frontRight;
    }

    public BufferedImage getRight() {
        return right;
    }

    public BufferedImage getBack() {
        return back;
    }
}
