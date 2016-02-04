package uk.ac.cam.teamdelta.robert;
import uk.ac.cam.teamdelta.*;

public class Frame {
    public Frame(ImageInputSet set, int junctions) {
        m_set = set;
        m_junctions = junctions;
    }
    public ImageInputSet getImages() {
        return m_set;
    }
    public int numJunctions() {
        return m_junctions;
    }
    private final ImageInputSet m_set;
    private final int m_junctions;
}
