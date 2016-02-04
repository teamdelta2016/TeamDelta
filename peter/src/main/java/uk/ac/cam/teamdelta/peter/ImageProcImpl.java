package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

class ImageProcImpl extends ImageProc {

    ImageProcImpl(ImageProcParams params) {
        super(params);
    }

    @Override
    public ImageOutputSet process(ImageInputSet input) {
        return new ImageOutputSet(copyImage(input.frontLeft),
                                  copyImage(input.left),
                                  copyImage(input.right),
                                  copyImage(input.back));
    }

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

}
