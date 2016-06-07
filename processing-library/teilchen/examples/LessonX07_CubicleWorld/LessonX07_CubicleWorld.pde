import teilchen.*;
import teilchen.behavior.*;
import teilchen.constraint.*;
import teilchen.cubicle.*;
import teilchen.force.*;
import teilchen.integration.*;
import teilchen.util.*;
import java.util.Vector;

final int WORLD_NUMBER_OF_CUBICLES = 15;
final float WORLD_CUBICLE_SCALE = 20;
final float WORLD_SCALE = WORLD_NUMBER_OF_CUBICLES * WORLD_CUBICLE_SCALE;
final boolean showCubicles = true;
float mRotationZ = 0.1f;
final PVector mPosition = new PVector();
CubicleWorld mCubicleWorld;
CubicleWorldView mCubicleWorldView;
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
    mCubicleWorldView.color_full = color(0, 4);
    mCubicleWorld.add(new MCubicleEntity());
}
void draw() {
    /* handle entities */
    if (frameRate > 30) {
        addRandomEntities(2);
    }
    mCubicleWorld.update();
    Vector<ICubicleEntity> mEntities = mCubicleWorld.getLocalEntities(mPosition, 1);
    /* draw things */
    background(255);
    pushMatrix();
    translate(width / 2, height / 2, 0);
    /* rotate */
    if (mousePressed) {
        mRotationZ += (mouseX * 0.01f - mRotationZ) * 0.05f;
    } else {
        mPosition.x = mouseX - width / 2;
        mPosition.y = mouseY - height / 2;
    }
    rotateX(THIRD_PI);
    rotateZ(mRotationZ);
    /* draw cubicle world */
    if (showCubicles) {
        strokeWeight(0.1f);
        stroke(0, 127);
        noFill();
        mCubicleWorldView.draw(g);
    }
    /* draw entities */
    int mNumberOfPointsSelected = 0;
    strokeWeight(0.5f);
    stroke(0, 127, 255, 127);
    noFill();
    if (mEntities != null) {
        mNumberOfPointsSelected = mEntities.size();
        for (ICubicleEntity mEntity : mEntities) {
            MCubicleEntity m = (MCubicleEntity) mEntity;
            stroke(m.mColor);
            DrawLib.cross3(g, mEntity.position(), 5.0f);
        }
    }
    /* draw crosshair */
    stroke(255, 0, 0, 63);
    noFill();
    beginShape(LINES);
    vertex(mPosition.x, -WORLD_SCALE / 2, 0);
    vertex(mPosition.x, WORLD_SCALE / 2, 0);
    vertex(-WORLD_SCALE / 2, mPosition.y, 0);
    vertex(WORLD_SCALE / 2, mPosition.y, 0);
    endShape();
    /* draw selection sphere */
    stroke(255, 0, 0, 63);
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
class MCubicleEntity
        implements ICubicleEntity {
    int mColor = color(0, 127, random(0, 255), 127);
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
    boolean leaving(int theX, int theY, int theZ) {
        return !(theX == cubicle().x
                 && theY == cubicle().y
                 && theZ == cubicle().z);
    }
    boolean isActive() {
        return true;
    }
}
