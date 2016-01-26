package uk.ac.cam.teamdelta.peter;

import uk.ac.cam.teamdelta.Constants;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

public class Test {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args){
        System.out.println("Test: " + Constants.appName);

        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());

        String javaLibPath = System.getProperty("java.library.path");
        System.out.println(javaLibPath);

        try {
            Mat source = Highgui.imread("/Users/work/Downloads/test1.jpeg",
                                        Highgui.CV_LOAD_IMAGE_COLOR);
            // Mat destination = new Mat(source.rows(),source.cols(),source.type());
            // Imgproc.equalizeHist(source, destination);

            Mat dest = source.clone();

            double[] adj = new double[]{3.0, 9.0, 50.0};
            for(int i=0; i<3; i++){
                // adj[i] *= 0.8;
            }

            Size sizeA = dest.size();
            for (int i = 0; i < sizeA.height; i++){
                for (int j = 0; j < sizeA.width; j++) {
                    double[] data = source.get(i, j);
                    data[0] = data[0] * adj[2] / (255.0);
                    data[1] = data[1] * adj[1] / (255.0);
                    data[2] = data[2] * adj[0] / (255.0);
                    dest.put(i, j, data);
                }
            }

            for (int i = 0; i < sizeA.height; i++){
                for (int j = 0; j < sizeA.width; j++) {
                    double[] dataA = source.get(i, j);
                    double[] dataB = dest.get(i, j);
                    for(int k=0; k<3; k++){
                        dataB[k] = dataA[k] + 0.8 * (dataB[k] - dataA[k]);
                    }
                    dest.put(i, j, dataB);
                }
            }


            int blurDim = 11;
            for (int i = blurDim/2; i < sizeA.height-blurDim/2; i++){
                for (int j = blurDim/2; j < sizeA.width-blurDim/2; j++) {
                    double[] result = dest.get(i, j);
                    for(int k=0; k<3; k++){
                        double sum = 0;
                        for(int x=0; x<blurDim; x++){
                            for(int y=0; y<blurDim; y++){
                                sum += dest.get(i + blurDim/2 - x, j + blurDim/2 - y)[k];
                            }
                        }
                        sum /= (blurDim * blurDim);
                        double orig = result[k];
                        double factor = (j - sizeA.width/2) / (sizeA.width/2);
                        if(factor < 0){
                            factor *= -1;
                        }
                        result[k] = orig + factor * (sum - orig);
                    }
                    dest.put(i, j, result);
                }
            }



            Highgui.imwrite("/Users/work/Downloads/test1out.jpg", dest);
        }catch (Exception e) {
            System.out.println("error: " + e.getMessage());
      }

    }

}
