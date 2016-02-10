package uk.ac.cam.teamdelta.andy;

import java.io.InputStream;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

public class RunnableFetcher extends Thread implements KeyInterface {
	private BufferedImage result; 
	private URL fetchLocation;
	public RunnableFetcher(int width, int height, double latitude, double longitude, 
						 int fov, int heading, int pitch) throws MalformedURLException {
		String url = "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" + 
					 height + "&location=" + latitude + "," + longitude + "&fov=" + fov + "&heading=" + 
					 heading + "&pitch=" + pitch + "&key=" + API_KEY;	
		fetchLocation = new URL(url);
		result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	@Override
	public void run() {
		InputStream in = null;
		try {
			in = fetchLocation.openStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			while((bytesRead = in.read(buffer)) > 0) {
				stream.write(buffer, 0, bytesRead);
			}
			stream.flush();
			stream.close();
			InputStream input = new ByteArrayInputStream(stream.toByteArray());
			result = ImageIO.read(input);
		} catch (IOException e) {
			System.err.println("Failed to fetch image.");
		} 
	}

	public BufferedImage getImage() {
		return result;
	}
}