package uk.ac.cam.teamdelta.robert;

import uk.ac.cam.teamdelta.ImageOutputSet;
import uk.ac.cam.teamdelta.JunctionInfo;

public class Frame {
    public Frame(ImageOutputSet set, JunctionInfo junctions) {
        m_set = set;
        m_junctions = junctions;
    }
    public ImageOutputSet getImages() {
        return m_set;
    }
    private final ImageOutputSet m_set;
    private final JunctionInfo m_junctions;
}
