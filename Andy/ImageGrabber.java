import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.net.URL;

public class ImageGrabber implements KeyInterface {
    private void sendGet(int width, int height, double latitude, double longitude,
                         int fov, int heading, int pitch) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" +
                     height + "&location=" + latitude + "," + longitude + "&fov=" + fov + "&heading=" +
                     heading + "&pitch=" + pitch + "&key=" + API_KEY;
        URL newUrl = new URL(url);
        InputStream in = newUrl.openStream();
        int i;
        OutputStream out = new BufferedOutputStream(new FileOutputStream("temp_image.jpg"));

        while((i = in.read()) != -1) {
            out.write(i);
        }
        in.close();
        out.close();
    }

    public static void main(String[] args) throws Exception {
        ImageGrabber ig = new ImageGrabber();

        int width = 400;
        int height = 400;
        double latitude = 40.720032;
        double longitude = -73.988354;
        int fov = 90;
        int heading = 235;
        int pitch = 10;
        ig.sendGet(width, height, latitude, longitude, fov, heading, pitch);
    }
}