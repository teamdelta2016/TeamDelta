import java.util.HashMap;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class ImageInputSet {
	private HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	public ImageInputSet(BufferedImage left, BufferedImage frontLeft, BufferedImage frontRight, 
						 BufferedImage right, BufferedImage back) {
		map.put("left", left);
		map.put("frontLeft", frontLeft);
		map.put("frontRight", frontRight);
		map.put("right", right);
		map.put("back", back);
		images.add(left);
		images.add(frontLeft);
		images.add(frontRight);
		images.add(right);
		images.add(back);
	}

	public BufferedImage getImage(String orientation) {
		return map.get(orientation);
	}

	public ArrayList<BufferedImage> getImages() {
		return images;
	}
}