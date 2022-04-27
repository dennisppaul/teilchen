package teilchen.constraint;

public class TriangleDeflectorIndexed
        extends TriangleDeflector {

    private final int a_index;

    private final int b_index;

    private final int c_index;

    private final float[] mVertices;

    public TriangleDeflectorIndexed(float[] pVertices, int pA, int pB, int pC) {
        super();
        mVertices = pVertices;
        a_index = pA;
        b_index = pB;
        c_index = pC;
        updateProperties();
    }

    public void updateProperties() {
        updateVertices();
        super.updateProperties();
    }

    private void updateVertices() {
        a().set(mVertices[a_index + 0],
                mVertices[a_index + 1],
                mVertices[a_index + 2]);
        b().set(mVertices[b_index + 0],
                mVertices[b_index + 1],
                mVertices[b_index + 2]);
        c().set(mVertices[c_index + 0],
                mVertices[c_index + 1],
                mVertices[c_index + 2]);
    }
}
