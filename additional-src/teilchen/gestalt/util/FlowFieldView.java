package teilchen.gestalt.util;

import gestalt.candidates.JoglDisposableBin;
import gestalt.context.GLContext;
import gestalt.impl.jogl.context.JoglGLContext;
import gestalt.impl.jogl.shape.JoglMaterial;
import gestalt.shape.AbstractShape;
import javax.media.opengl.GL;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import teilchen.force.flowfield.FlowField;
import teilchen.force.flowfield.FlowFieldForce;


/**
 *
 * @author patrick
 */
public class FlowFieldView extends AbstractShape {

    private FlowField _myFlowField;

    private JoglDisposableBin _myViewBin;

    public boolean drawVectors;


    public FlowFieldView(FlowField theField) {
        _myFlowField = theField;
        material = new JoglMaterial();
        scale.set(theField.scale());
        transform = new TransformMatrix4f(theField.transform());
        _myViewBin = new JoglDisposableBin();
        _myViewBin.fill = false;
        _myViewBin.wireframe = true;
    }


    public void draw(GLContext theRenderContext) {
        GL gl = ((JoglGLContext) theRenderContext).gl;

        material.begin(theRenderContext);

        /* draw vectors */
        if (drawVectors) {
            for (int y = 0; y <= _myFlowField.n; y++) {
                for (int x = 0; x <= _myFlowField.n; x++) {
                    float myRatioX = x / (float) (_myFlowField.n);
                    float myRatioY = y / (float) (_myFlowField.n);
                    Vector3f myCellCenter = new Vector3f(myRatioX * scale.x, myRatioY * scale.y, scale.z / 2f);
                    _myViewBin.point(myCellCenter);

                    Vector3f myVelocity = new Vector3f(_myFlowField.u()[x][y], _myFlowField.v()[x][y]);
                    myVelocity.scale(3);
                    myVelocity.add(myCellCenter);
                    _myViewBin.line(myCellCenter, myVelocity);
                }
            }
        }

        /* draw bounding box */
        _myViewBin.box(_myFlowField.position(), _myFlowField.scale());

        /* draw forces */
        for (FlowFieldForce myForce : _myFlowField.forces()) {
            float myScale = _myFlowField.scale().x / _myFlowField.n;
            _myViewBin.circle(myForce.position, myForce.range * myScale);
        }

        _myViewBin.draw(theRenderContext);

        material.end(theRenderContext);
    }
}
