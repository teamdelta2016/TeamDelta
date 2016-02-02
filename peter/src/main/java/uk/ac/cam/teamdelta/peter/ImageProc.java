package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.ImageProcParams;

public abstract class ImageProc {

    private final ImageProcParams params;

    public ImageProc(ImageProcParams params){
        this.params = params;
    }

    public abstract ImageOutputSet process(ImageInputSet input);

}
