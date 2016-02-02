package uk.ac.cam.teamdelta.peter;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;
import java.awt.image.*;

import java.io.*;
import javax.imageio.*;

protected class Stitcher {

    private static final int SEARCH_RAD = 20; /* maximum stitch deviation searched */
    private static final int SEARCH_CHUNK_SIZE = 10; /* Size of y chunk to search */
    private static final int SEARCH_SKIP_SIZE = 10; /* Size of y chunk to skip */

    protected static Mat stitchImages(Mat sourceLeft, Mat sourceRight){
        int yOffset = findYOffset(sourceLeft, sourceRight);



        int newHeight = sourceLeft.rows() - (yOffset < 0 ? -yOffset : yOffset);
        Mat joined = new Mat(newHeight, sourceLeft.cols() + sourceRight.cols(), sourceLeft.type());
        Mat newLeft = sourceLeft.submat(yOffset < 0 ? 0 : yOffset,
                                        sourceLeft.rows() - (yOffset < 0 ? -yOffset : 0),
                                        0, sourceLeft.cols());
        Mat newRight = sourceRight.submat(yOffset < 0 ? -yOffset : 0,
                                          sourceRight.rows() - (yOffset < 0 ? 0 : yOffset),
                                          0, sourceRight.cols());

        Mat joinedLeft = joined.submat(0, joined.rows(), 0, sourceLeft.cols());
        Mat joinedRight = joined.submat(0, joined.rows(), sourceLeft.cols(), joined.cols());

        newLeft.copyTo(joinedLeft);
        newRight.copyTo(joinedRight);

        return joined;
    }

    private static int findYOffset(Mat left, Mat right){
        int minY = 0;
        double minYVal = -1;

        for(int i = 0; i < SEARCH_RAD * 2; i++){
            int yO = i % 2 == 0 ? i / 2 : -i / 2;

            int xL = ((int)left.cols()) - 1;
            int xR = 0;

            double sum = 0;
            int currChunk = 0;
            for(int y = SEARCH_RAD; y < left.rows() - SEARCH_RAD; y++){
                if(currChunk >= SEARCH_CHUNK_SIZE){
                    currChunk = 0;
                    y += SEARCH_SKIP_SIZE - 1;
                    continue;
                }else{
                    currChunk++;
                }

                double colL = left.get(y + yO, xL)[0];
                double colR = right.get(y, xR)[0];
                double dCol = colL - colR;
                sum += dCol * dCol;
            }

            if(minYVal < 0 || sum < minYVal){
                minYVal = sum;
                minY = yO;
            }
        }

        return minY;
    }

}
