import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to use `CubicleWorld` to separate a given space into equally
 * sized cubes in order to only draw paticles from a specific cube. this mechanism is helpful
 * to avoid decrease the in demand for computational resources in particle systems with large
 * numbers of particles.
 *
 * move or drag mouse to rotate view.
 */

final float WORLD_CUBICLE_SCALE = 20;

final int WORLD_NUMBER_OF_CUBICLES = 15;

final float WORLD_SCALE = WORLD_NUMBER_OF_CUBICLES * WORLD_CUBICLE_SCALE;

CubicleWorld mCubicleWorld;

CubicleWorldView mCubicleWorldView;

final PVector mPosition = new PVector();

float mRotationZ = 0.1f;

final boolean mShowCubicles = true;

void settings() {
    size(640, 480, P3D);
}

void setup() {
    textFont(createFont("Courier", 11));
    hint(DISABLE_DEPTH_SORT);
    hint(DISABLE_DEPTH_TEST);
    /* setup world */
    mCubicleWorld = new CubicleWorld(WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES, WORLD_NUMBER_OF_CUBICLES);
    mCubicleWorld.cellscale().set(WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE, WORLD_CUBICLE_SCALE);
    mCubicleWorld.transform().translation.set(-WORLD_SCALE / 2, -WORLD_SCALE / 2, -WORLD_SCALE / 2);
    mCubicleWorldView = new CubicleWorldView(mCubicleWorld);
    mCubicleWorldView.color_empty = color(0, 1);
    mCubicleWorldView.color_full = color(0, 2);
    mCubicleWorld.add(new MCubicleEntity());
}

void draw() {
    /* add entities */
    addRandomEntities(10);
    mCubicleWorld.update();
    ArrayList<ICubicleEntity> mEntities = mCubicleWorld.getLocalEntities(mPosition, 1);
    /* draw things */
    background(255);
    pushMatrix();
    translate(width / 2.0f, height / 2.0f, 0);
    /* rotate */
    if (mousePressed) {
        mRotationZ += (mouseX * 0.01f - mRotationZ) * 0.05f;
    } else {
        mPosition.x = mouseX - width / 2.0f;
        mPosition.y = mouseY - height / 2.0f;
    }
    rotateX(THIRD_PI);
    rotateZ(mRotationZ);
    /* draw cubicle world */
    if (mShowCubicles) {
        strokeWeight(1.0f / mCubicleWorld.cellscale().x); // unscale stroke weight
        noFill();
        mCubicleWorldView.draw(g);
    }
    /* draw entities */
    int mNumberOfPointsSelected = 0;
    noFill();
    if (mEntities != null) {
        mNumberOfPointsSelected = mEntities.size();
        for (ICubicleEntity mEntity : mEntities) {
            MCubicleEntity m = (MCubicleEntity) mEntity;
            strokeWeight(m.weight);
            stroke(0, 127);
            DrawLib.cross3(g, mEntity.position(), 10.0f);
        }
    }
    /* draw crosshair */
    strokeWeight(3);
    stroke(0, 63);
    noFill();
    beginShape(LINES);
    vertex(mPosition.x, -WORLD_SCALE / 2, 0);
    vertex(mPosition.x, WORLD_SCALE / 2, 0);
    vertex(-WORLD_SCALE / 2, mPosition.y, 0);
    vertex(WORLD_SCALE / 2, mPosition.y, 0);
    endShape();
    /* draw selection sphere */
    strokeWeight(1);
    stroke(0, 63);
    noFill();
    translate(mPosition.x, mPosition.y, 0);
    box(WORLD_CUBICLE_SCALE);
    popMatrix();
    fill(0);
    noStroke();
    text("POINTS   : " + mCubicleWorld.entities().size(), 10, 12);
    text("SELECTED : " + mNumberOfPointsSelected, 10, 24);
    text("FPS      : " + frameRate, 10, 36);
}

void addRandomEntities(int pNumberParticles) {
    for (int i = 0; i < pNumberParticles; i++) {
        MCubicleEntity mEntity = new MCubicleEntity();
        mEntity.position().x = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
        mEntity.position().y = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
        mEntity.position().z = random(-WORLD_SCALE / 2, WORLD_SCALE / 2);
        mCubicleWorld.add(mEntity);
    }
}
class MCubicleEntity implements ICubicleEntity {
    float weight = random(1, 5);
    
final Vector3i mCubiclePosition;
    
final PVector mPosition;
    
MCubicleEntity() {
        mCubiclePosition = new Vector3i();
        mPosition = new PVector();
    }
    
Vector3i cubicle() {
        return mCubiclePosition;
    }
    
PVector position() {
        return mPosition;
    }
    
boolean leaving(int pX, int pY, int pZ) {
        return !(pX == cubicle().x && pY == cubicle().y && pZ == cubicle().z);
    }
    
boolean isActive() {
        return true;
    }
}
