package teilchen.gestalt.test;

import gestalt.candidates.JoglDisposableBin;
import gestalt.render.AnimatorRenderer;
import mathematik.Util;
import mathematik.Vector3f;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.force.Gravity;
import teilchen.force.flowfield.FlowField;
import teilchen.force.flowfield.FlowFieldForceMOUSE;
import teilchen.gestalt.util.FlowFieldView;


/**
 *
 * @author patrick
 */
public class TestFluidField extends AnimatorRenderer {

    private Physics _myParticleSystem;

    private FlowField _myFlowField;

    private FlowFieldView _myFluidFieldView;

    private FlowFieldForceMOUSE _myForce;

    private JoglDisposableBin _myViewBin;


    public void setup() {
        cameramover(true);

        _myForce = new FlowFieldForceMOUSE();
        _myForce.range = 2;
        _myFlowField = new FlowField(20, 20, new Vector3f(600, 200, 30));
        _myFlowField.addForce(_myForce);
        _myFlowField.forceScale = 1;
        _myFluidFieldView = new FlowFieldView(_myFlowField);
        _myFluidFieldView.drawVectors = true;

        Gravity myGravity = new Gravity();
        myGravity.force().y = 0;
        _myParticleSystem = new Physics();
        _myParticleSystem.add(myGravity);
        _myParticleSystem.add(_myFlowField);
        for (int i = 0; i < 1000; i++) {
            _myParticleSystem.makeParticle(new Vector3f(Util.random(0, _myFlowField.scale().x),
                    Util.random(0, _myFlowField.scale().y), 0));
        }

        _myViewBin = new JoglDisposableBin();
        _myViewBin.color().set(1f, 0f, 0f, 1f);

        bin(BIN_3D).add(_myFluidFieldView);
        bin(BIN_3D).add(_myViewBin);
    }


    private void drawParticles() {
        for (Particle myParticle : _myParticleSystem.particles()) {
            _myViewBin.point(myParticle.position());
            if (myParticle.position().x < _myFlowField.position().x ||
                    myParticle.position().x > _myFlowField.position().x + _myFlowField.scale().x ||
                    myParticle.position().y < _myFlowField.position().y ||
                    myParticle.position().y > _myFlowField.position().y + _myFlowField.scale().y) {
                myParticle.position().set(Util.random(0, _myFlowField.scale().x),
                        Util.random(0, _myFlowField.scale().y), 0);
                myParticle.force().set(0,0,0);
                myParticle.velocity().set(0,0,0);
            }
        }
    }


    public void loop(final float theDeltaTime) {
        _myFlowField.loop(theDeltaTime);
        _myParticleSystem.step(theDeltaTime);

        if (event().mouseDown) {
            Vector3f myPosition = new Vector3f(event().normalized_mouseX, 1f - event().normalized_mouseY);
            myPosition.scale(_myFlowField.scale());
            _myForce.setPosition(myPosition);
            _myForce.range = 2;
        }

        drawParticles();
    }


    public static void main(String[] args) {
        new TestFluidField().init();
    }
}
