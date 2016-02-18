package uk.ac.cam.teamdelta.andy;

import uk.ac.cam.teamdelta.ImageInputSet;

import java.io.IOException;
import java.net.MalformedURLException;

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

	}
}
