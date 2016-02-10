package uk.ac.cam.teamdelta;

import java.awt.image.BufferedImage;

public class ImageInputSet {
    private final BufferedImage left, frontLeft, frontRight, right, back;

    public ImageInputSet(
        BufferedImage left_,
        BufferedImage frontLeft_,
        BufferedImage frontRight_,
        BufferedImage right_,
        BufferedImage back_
    ) {
        left = left_;
        frontLeft = frontLeft_;
        frontRight = frontRight_;
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
