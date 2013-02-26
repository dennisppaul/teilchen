package teilchen.force;


import mathematik.Intersection;
import mathematik.Intersection.Result;
import mathematik.Vector3f;
import mathematik.WorldAxisAlignedBoundingBox;

import teilchen.Particle;
import teilchen.Physics;


public class TriangleDeflector
    implements IForce {

    private final Vector3f a;

    private final Vector3f b;

    private final Vector3f c;

    private final Vector3f _myNormal;

    private float _myCoefficientOfRestitution;

    private final Vector3f _myTempReflectionVector;

    private final Vector3f _myTempNormalComponent;

    private final Vector3f _myTempTangentComponent;

    private final Result _myTempResult;

    private final Vector3f myTempPointOfIntersection = new Vector3f();

    private final WorldAxisAlignedBoundingBox _myWorldAxisAlignedBoundingBox;

    private final Vector3f[] _myVectorCollection;

    public boolean AUTO_UPDATE = true;

    private boolean _myGotHit = false;

    private boolean _myActive;

    public TriangleDeflector() {
        a = new Vector3f();
        b = new Vector3f();
        c = new Vector3f();

        /* hmmm. */
        _myVectorCollection = new Vector3f[3];
        _myVectorCollection[0] = a;
        _myVectorCollection[1] = b;
        _myVectorCollection[2] = c;

        _myNormal = new Vector3f();
        _myCoefficientOfRestitution = 1.0f;

        _myTempReflectionVector = new Vector3f();
        _myTempNormalComponent = new Vector3f();
        _myTempTangentComponent = new Vector3f();
        _myTempResult = new Result();
        _myWorldAxisAlignedBoundingBox = new WorldAxisAlignedBoundingBox();

        _myActive = true;
    }


    public Vector3f a() {
        return a;
    }


    public Vector3f b() {
        return b;
    }


    public Vector3f c() {
        return c;
    }


    public WorldAxisAlignedBoundingBox boundingbox() {
        return _myWorldAxisAlignedBoundingBox;
    }


    public void updateProperties() {
        mathematik.Util.calculateNormal(a, b, c, _myNormal);
        mathematik.Util.updateBoundingBox(_myWorldAxisAlignedBoundingBox, _myVectorCollection);
    }


    public void apply(final float theDeltaTime, final Physics theParticleSystem) {

        /* update triangle properties -- maybe this is better not done automatically */
        if (AUTO_UPDATE) {
            updateProperties();
        }

        _myGotHit = false;
        for (final Particle myParticle : theParticleSystem.particles()) {
            if (!myParticle.fixed()) {
                /* adjust boundingbox width to particle velocity to avoid particle shooting through the boundingbox */
                Vector3f myTempBoundingBoxScale = new Vector3f(_myWorldAxisAlignedBoundingBox.scale);
                if (myParticle.velocity().x > _myWorldAxisAlignedBoundingBox.scale.x) {
                    _myWorldAxisAlignedBoundingBox.scale.x = myParticle.velocity().x;
                }
                if (myParticle.velocity().y > _myWorldAxisAlignedBoundingBox.scale.y) {
                    _myWorldAxisAlignedBoundingBox.scale.y = myParticle.velocity().y;
                }
                if (myParticle.velocity().z > _myWorldAxisAlignedBoundingBox.scale.z) {
                    _myWorldAxisAlignedBoundingBox.scale.z = myParticle.velocity().z;
                }

                /* only test if in bounding box */
                if (mathematik.Util.contains(myParticle.position(), _myWorldAxisAlignedBoundingBox)) {
                    final Vector3f myRay;
                    boolean USE_NORMAL = true;
                    if (USE_NORMAL) {
                        myRay = mathematik.Util.scale(_myNormal, -myParticle.velocity().length());
                    } else {
                        myRay = myParticle.velocity();
                    }
                    final boolean mySucces = Intersection.intersectRayTriangle(myParticle.position(),
                                                                               myRay,
                                                                               a, b, c,
                                                                               _myTempResult,
                                                                               true);
                    /* is particle past plane. */
                    if (mySucces && _myTempResult.t < 0) {
                        myTempPointOfIntersection.set(myParticle.velocity());
                        myTempPointOfIntersection.scale(_myTempResult.t);
                        myTempPointOfIntersection.add(myParticle.position());
                        myParticle.position().set(myTempPointOfIntersection);

                        /* change direction */
                        if (_myCoefficientOfRestitution != 0) {
                            seperateComponents(myParticle, _myNormal);
                            myParticle.velocity().set(_myTempReflectionVector);
                        } else {
                            seperateComponents(myParticle, _myNormal);
                            myParticle.velocity().set(_myTempReflectionVector);
                        }

                        _myGotHit = true;
                        myParticle.tag(true);
                        markParticle(myParticle);
                    }
                }

                /* reset boundingbox scale */
                _myWorldAxisAlignedBoundingBox.scale.set(myTempBoundingBoxScale);
            }
        }
    }


    protected void markParticle(Particle theParticle) {
    }


    public boolean hit() {
        return _myGotHit;
    }


    public void coefficientofrestitution(float theCoefficientOfRestitution) {
        _myCoefficientOfRestitution = theCoefficientOfRestitution;
    }


    public float coefficientofrestitution() {
        return _myCoefficientOfRestitution;
    }


    private final void seperateComponents(Particle theParticle, Vector3f normal) {
        /* normal */
        _myTempNormalComponent.set(normal);
        _myTempNormalComponent.scale(normal.dot(theParticle.velocity()));
        /* tangent */
        _myTempTangentComponent.sub(theParticle.velocity(), _myTempNormalComponent);
        /* negate normal */
        _myTempNormalComponent.scale( -_myCoefficientOfRestitution);
        /* set reflection vector */
        _myTempReflectionVector.add(_myTempTangentComponent, _myTempNormalComponent);
    }


    public Vector3f normal() {
        return _myNormal;
    }


    public boolean dead() {
        return false;
    }


    public boolean active() {
        return _myActive;
    }


    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
