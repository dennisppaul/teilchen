package teilchen.force;

public class TriangleDeflectorIndexed
        extends TriangleDeflector {

    private final int a_index;

    private final int b_index;

    private final int c_index;

    private final float[] _myVertices;

    public TriangleDeflectorIndexed(float[] theVertices, int theA, int theB, int theC) {
        super();
        _myVertices = theVertices;
        a_index = theA;
        b_index = theB;
        c_index = theC;
        updateProperties();
    }

    public void updateProperties() {
        updateVertices();
        super.updateProperties();
    }

    private void updateVertices() {
        a().set(_myVertices[a_index + 0],
                _myVertices[a_index + 1],
                _myVertices[a_index + 2]);
        b().set(_myVertices[b_index + 0],
                _myVertices[b_index + 1],
                _myVertices[b_index + 2]);
        c().set(_myVertices[c_index + 0],
                _myVertices[c_index + 1],
                _myVertices[c_index + 2]);
    }
}
