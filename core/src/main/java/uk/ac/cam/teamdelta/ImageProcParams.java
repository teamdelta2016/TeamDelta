package uk.ac.cam.teamdelta;

public class ImageProcParams {

    public final int blurValue;
    public final double ghostFactor;
    public final double nightTimeFactor;
    public final double darkEdgesFactor;

    public ImageProcParams(int blurValue,
                           double ghostFactor,
                           double nightTimeFactor,
                           double darkEdgesFactor){
        this.blurValue = blurValue;
        this.ghostFactor = ghostFactor;
        this.nightTimeFactor = nightTimeFactor;
        this.darkEdgesFactor = darkEdgesFactor;
    }

}
