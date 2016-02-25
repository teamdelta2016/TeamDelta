package uk.ac.cam.teamdelta.peter;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

class Utils {

    static final int FRONT_ROWS = 640;
    static final int FRONT_COLS = 640 * 2;

    static final int MAT_TYPE = 16;

    static void blurImage(Mat source, int amount){
        int radius = amount * 2 + 1;
        Imgproc.GaussianBlur(source, source, new Size(radius, radius), radius);
    }

    static Mat overlayImage(Mat bottom, Mat top, double opacity){
        Mat img = new Mat();
        Core.addWeighted(bottom, 1.0 - opacity, top, opacity, 0.0, img);
        return img;
    }

    static Mat stretchImage(Mat source, double factorX, double factorY){
        Mat img = new Mat();
        Size orig = source.size();
        Size sz = new Size(orig.width * factorX, orig.height * factorY);
        Imgproc.resize(source, img, sz);
        Rect roi = new Rect((int)(orig.width * (factorX-1.0) / 2.0),
                            (int)(orig.height * (factorY-1.0)/2.0),
                            (int)orig.width,
                            (int)orig.height);

        return new Mat(img, roi);
    }

    static Mat resizeImage(Mat source, int rows, int cols){
        Mat image = new Mat();
        Size size = new Size(cols, rows);
        Imgproc.resize(source, image, size);
        return image;
    }

    static Mat bufferedImageToMat(BufferedImage bi) {
        if(bi.getType() != BufferedImage.TYPE_3BYTE_BGR){
            BufferedImage newBi = new BufferedImage(bi.getWidth(),
                                                    bi.getHeight(),
                                                    BufferedImage.TYPE_3BYTE_BGR);
            newBi.getGraphics().drawImage(bi, 0, 0, null);
            bi = newBi;
        }

        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        DataBuffer buffer = bi.getRaster().getDataBuffer();
        if(buffer.getDataType() == DataBuffer.TYPE_BYTE){
            mat.put(0, 0, ((DataBufferByte) buffer).getData());
        }else{
            mat.put(0, 0, ((DataBufferInt) buffer).getData());
        }
        return mat;
    }

    static BufferedImage matToBufferedImage(Mat mat) throws IOException{
        MatOfByte bytemat = new MatOfByte();
        Highgui.imencode(".jpg", mat, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = ImageIO.read(in);
        return img;
    }

}
