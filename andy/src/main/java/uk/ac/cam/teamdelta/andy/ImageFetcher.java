package uk.ac.cam.teamdelta.andy;

import uk.ac.cam.teamdelta.ImageInputSet;

import java.io.IOException;
import java.net.MalformedURLException;

import java.io.File;
import javax.imageio.ImageIO;

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
		// ImageFetcher imageFetch = new ImageFetcher();
		// long first = System.currentTimeMillis();
		// ImageInputSet result = imageFetch.sendGet(400, 400, 40.720032, -73.988354, 90, 0, 10);
		// long second = System.currentTimeMillis();
		// long third = second - first;
		// System.out.println(third);
		// File leftFile = new File("left.jpg");
		// File leftFrontFile = new File("leftFront.jpg");
		// File rightFrontFile = new File("rightFront.jpg");
		// File rightFile = new File("right.jpg");
		// File backFile = new File("back.jpg");
		// ImageIO.write(result.getLeft(), "jpg", leftFile);
		// ImageIO.write(result.getFrontLeft(), "jpg", leftFrontFile);
		// ImageIO.write(result.getFrontRight(), "jpg", rightFrontFile);
		// ImageIO.write(result.getRight(), "jpg", rightFile);
		// ImageIO.write(result.getBack(), "jpg", backFile);
	}
}
