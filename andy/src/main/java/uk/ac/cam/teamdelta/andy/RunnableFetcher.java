package uk.ac.cam.teamdelta.andy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunnableFetcher extends Thread {

    private BufferedImage result;
    private URL fetchLocation;

    public RunnableFetcher(int width, int height, double latitude, double longitude,
                           int fov, int heading, int pitch) throws MalformedURLException {
        try {
            String API_KEY = new String(Files.readAllBytes(Paths.get(
                    "./src/main/resources/uk.ac.cam.teamdelta.larry/apikey.txt"))).trim();

            String url = "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" +
                    height + "&location=" + latitude + "," + longitude + "&fov=" + fov + "&heading=" +
                    heading + "&pitch=" + pitch + "&key=" + API_KEY;
            fetchLocation = new URL(url);
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            result = ImageIO.read(fetchLocation);
        } catch (IOException e) {
            System.err.println("Failed to fetch image.");
        }
    }

    public BufferedImage getImage() {
        return result;
    }
}