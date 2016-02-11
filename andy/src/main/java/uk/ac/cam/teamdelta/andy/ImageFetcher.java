package uk.ac.cam.teamdelta.andy;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import java.lang.InterruptedException;
import java.io.IOException;
import java.io.File;
import uk.ac.cam.teamdelta.*;

public class ImageFetcher {
	public ImageInputSet sendGet(int width, int height, double latitude, double longitude, 
								 int fov, int heading, int pitch) throws MalformedURLException {
		RunnableFetcher[] results = {new RunnableFetcher(width, height, latitude, longitude, fov, heading - 30, pitch),
								 	 new RunnableFetcher(width, height, latitude, longitude, fov, heading + 30, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, heading - 90, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, heading + 90, pitch),
									 new RunnableFetcher(width, height, latitude, longitude, fov, heading + 180,pitch)};
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
		//ImageFetcher imageFetch = new ImageFetcher();
		//ImageInputSet result = imageFetch.sendGet(400, 400, 40.720032, -73.988354, 90, 10);
		//File leftFile = new File("left.jpg");
		//File leftFrontFile = new File("leftFront.jpg");
		//File rightFrontFile = new File("rightFront.jpg");
		//File rightFile = new File("right.jpg");
		//File backFile = new File("back.jpg");
		//ImageIO.write(result.getLeft(), "jpg", leftFile);
		//ImageIO.write(result.getFrontLeft(), "jpg", leftFrontFile);
		//ImageIO.write(result.getFrontRight(), "jpg", rightFrontFile);
		//ImageIO.write(result.getRight(), "jpg", rightFile);
		//ImageIO.write(result.getBack(), "jpg", backFile);
	}
}
