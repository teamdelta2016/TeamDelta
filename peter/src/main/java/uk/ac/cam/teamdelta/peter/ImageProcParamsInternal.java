package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.ImageProcParams;

public class ImageProcParamsInternal {

    private static int BLUR_VALUE_MIN = 0;
    private static int BLUR_VALUE_MAX = 14;

    private static double GHOST_FACTOR_MIN = 1;
    private static double GHOST_FACTOR_MAX = 2;

    private static double NIGHT_TIME_FACTOR_MIN = 0;
    private static double NIGHT_TIME_FACTOR_MAX = 0.95;

    private static double DARK_EDGES_FACTOR_MIN = 0;
    private static double DARK_EDGES_FACTOR_MAX = 1;

    public final int blurValue;
    public final double ghostFactor;
    public final double nightTimeFactor;
    public final double darkEdgesFactor;
    public final boolean showHeadlights;

    public ImageProcParamsInternal(ImageProcParams params){
        this.blurValue = (int) scaleFactor(params.blurValue,
                                     (double) BLUR_VALUE_MIN,
                                     (double) BLUR_VALUE_MAX);
        this.ghostFactor = scaleFactor(params.ghostFactor,
                                       GHOST_FACTOR_MIN,
                                       GHOST_FACTOR_MAX);
        this.nightTimeFactor = scaleFactor(params.nightTimeFactor,
                                           NIGHT_TIME_FACTOR_MIN,
                                           NIGHT_TIME_FACTOR_MAX);
        this.darkEdgesFactor = scaleFactor(params.darkEdgesFactor,
                                           DARK_EDGES_FACTOR_MIN,
                                           DARK_EDGES_FACTOR_MAX);
        this.showHeadlights = this.nightTimeFactor > 0.6;
    }

    private double scaleFactor(double val, double min, double max){
        return min + ((max - min) * val / 100);
    }

}
