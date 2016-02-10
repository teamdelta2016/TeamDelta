package uk.ac.cam.teamdelta;

import java.awt.image.BufferedImage;

public class ImageOutputSet {
    public ImageOutputSet(
        BufferedImage front_,
        BufferedImage left_,
        BufferedImage right_,
        BufferedImage back_
    ) {
        front = front_;
        left = left_;
        right = right_;
        back = back_;
    }

    public final BufferedImage front, left, right, back;
}
