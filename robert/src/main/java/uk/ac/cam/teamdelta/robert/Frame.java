package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.ImageInputSet;
import uk.ac.cam.teamdelta.JunctionInfo;

public class Frame
{
	public Frame(ImageInputSet set, JunctionInfo junctions)
	{
		m_set = set;
		m_junctions = junctions;
	}
	public ImageInputSet getImages() {return m_set;}
	//public JunctionInfo numJunctions() {return m_junctions;}
	private final ImageInputSet m_set;
	private final JunctionInfo m_junctions;
}
