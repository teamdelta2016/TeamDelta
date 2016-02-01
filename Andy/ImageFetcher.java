import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

public class ImageFetcher implements KeyInterface {
	private void sendGet(int width, int height, double latitude, double longitude, 
						 int fov, int heading, int pitch) throws Exception {
		String url = "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" + 
					 height + "&location=" + latitude + "," + longitude + "&fov=" + fov + "&heading=" + 
					 heading + "&pitch=" + pitch + "&key=" + API_KEY;		
		URL newUrl = new URL(url);
		InputStream in = null;
		try {
			in = newUrl.openStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			while((bytesRead = in.read(buffer)) > 0) {
				stream.write(buffer, 0, bytesRead);
			}
			BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			ImageIO.write(result, "jpg", stream);
		} catch (IOException e) {
			System.err.println("Failed to fetch image.");
		} finally {
			if(in != null) {
				in.close();
			}
		}
	}
}