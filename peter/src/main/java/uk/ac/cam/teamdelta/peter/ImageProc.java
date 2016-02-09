package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;
import java.awt.image.BufferedImage;

public abstract class ImageProc {

    protected final ImageProcParamsInternal params;

    protected ImageProc(ImageProcParams params){
        this.params = new ImageProcParamsInternal(params);
    }

    /**
     * Factory method to create and return a new ImageProc instance.
     *
     * This is to protect the constructor of the ImageProcImpl.
     *
     * @param params The image processing parameters.
     *
     * @return A new instance of an image processor.
     */
    public static ImageProc getImageProc(ImageProcParams params) {
        return new ImageProcImpl(params);
    }

    /**
     * Processes the set of five images in the input set.
     *
     * This method blocks and returns the result. Treat as a possibly
     * lengthy method.
     *
     * The returned images are guarenteed to not be in place of the
     * input images, so it is safe to deallocate the input images
     * after this method call.
     *
     * @param input The input set of five images
     * @param isJunction if the front image contains a junction (means
     *                   no headlights will be shown)
     *
     * @return The resulting four images
     */
    public abstract ImageOutputSet process(ImageInputSet input, boolean isJunction);

    /**
     * Processes two images, front left and front right. It stitches
     * these and returns a windscreen image based on the given parameters.
     *
     * @param left windscreen left image
     * @param right windscreen right image
     * @param params the parameters used to process the image
     *
     * @return the windscreen image
     */
    public static BufferedImage processTest(BufferedImage left,
                                            BufferedImage right,
                                            ImageProcParams params){
        ImageInputSet is = new ImageInputSet(left, right, null, null, null);
        ImageProc ip = ImageProc.getImageProc(params);
        ImageOutputSet os = ip.process(is, false);
        return os.front;
    }
}
