package uk.ac.cam.teamdelta;
import java.awt.Image;

public class ImageInputSet
{
	public ImageInputSet
	(
		Image frontLeft_,
		Image frontRight_,
		Image left_,
		Image right_,
		Image back_
	)
	{
		frontLeft = frontLeft_;
		frontRight = frontRight_;
		left = left_;
		right = right_;
		back = back_;
	}
	public final Image frontLeft, frontRight, left, right, back;
}
