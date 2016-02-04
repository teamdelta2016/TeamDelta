import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import java.lang.InterruptedException;
import java.io.IOException;
import java.io.File;

public class ImageFetcher {
	public ImageInputSet sendGet(int width, int height, double latitude, double longitude, 
								 int fov, int pitch) throws MalformedURLException {
		RunnableFetcher[] results = {new RunnableFetcher(width, height, latitude, longitude, fov, 260, pitch),
								 	 new RunnableFetcher(width, height, latitude, longitude, fov, 320, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, 40, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, 100, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, 180, pitch)};
		for(RunnableFetcher result : results) {
			result.start();
		}

		for(RunnableFetcher result : results) {
			try {
				result.join();
			} catch (InterruptedException e) {
			}
		}

		return new ImageInputSet(results[0].getImage(), results[1].getImage(), results[2].getImage(), 
							   	 results[3].getImage(), results[4].getImage());
	}

	public static void main(String[] args) throws IOException {
		ImageFetcher imageFetch = new ImageFetcher();
		ImageInputSet result = imageFetch.sendGet(400, 400, 40.720032, -73.988354, 90, 10);
		File leftFile = new File("left.jpg");
		File leftFrontFile = new File("leftFront.jpg");
		File rightFrontFile = new File("rightFront.jpg");
		File rightFile = new File("right.jpg");
		File backFile = new File("back.jpg");
		ImageIO.write(result.getImage("left"), "jpg", leftFile);
		ImageIO.write(result.getImage("frontLeft"), "jpg", leftFrontFile);
		ImageIO.write(result.getImage("frontRight"), "jpg", rightFrontFile);
		ImageIO.write(result.getImage("right"), "jpg", rightFile);
		ImageIO.write(result.getImage("back"), "jpg", backFile);	
	}
}
