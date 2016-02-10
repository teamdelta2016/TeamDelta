package uk.ac.cam.teamdelta;

public class ImageProcParams {

    public final double blurValue;
    public final double ghostFactor;
    public final double nightTimeFactor;
    public final double darkEdgesFactor;
    public final boolean showHeadlights;

    public ImageProcParams(double blurValue,
                           double ghostFactor,
                           double nightTimeFactor,
                           double darkEdgesFactor,
                           boolean showHeadlights){
        this.blurValue = blurValue;
        this.ghostFactor = ghostFactor;
        this.nightTimeFactor = nightTimeFactor;
        this.darkEdgesFactor = darkEdgesFactor;
        this.showHeadlights = showHeadlights;
    }

}
