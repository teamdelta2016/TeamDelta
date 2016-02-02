package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;

public abstract class ImageProc {

    private final ImageProcParams params;

    private ImageProc(ImageProcParams params){
        this.params = params;
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
    public static getImageProc(ImageProcParams params){
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
     *
     * @return The resulting four images
     */
    public abstract ImageOutputSet process(ImageInputSet input);

}
