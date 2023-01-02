import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 

/*
 * this sketch demonstrates how to create a complex swarm behavior by combining the four simple
 * behaviors `Separation`, `Alignment`, `Cohesion` and `Wander` ( plus `Motor` ).
 */

Physics mPhysics;

ArrayList<SwarmEntity> mSwarmEntities;

void settings() {
    size(640, 480, P3D);
}

void setup() {
    rectMode(CENTER);
    hint(DISABLE_DEPTH_TEST);
    /* physics */
    mPhysics = new Physics();
    Teleporter mTeleporter = new Teleporter();
    mTeleporter.min().set(0, 0, height / -2.0f);
    mTeleporter.max().set(width, height, height / 2.0f);
    mPhysics.add(mTeleporter);
    ViscousDrag mViscousDrag = new ViscousDrag();
    mPhysics.add(mViscousDrag);
    /* setup entities */
    mSwarmEntities = new ArrayList();
    for (int i = 0; i < 60; i++) {
        SwarmEntity mSwarmEntity = new SwarmEntity();
        mSwarmEntity.position()
                    .set(random(mTeleporter.min().x, mTeleporter.max().x),
                         random(mTeleporter.min().y, mTeleporter.max().y),
                         random(mTeleporter.min().z, mTeleporter.max().z));
        mSwarmEntities.add(mSwarmEntity);
        mPhysics.add(mSwarmEntity);
    }
}

void draw() {
    final float mDeltaTime = 1.0f / frameRate;
    /* physics */
    mPhysics.step(mDeltaTime);
    /* entities */
    for (SwarmEntity s : mSwarmEntities) {
        s.update();
    }
    /* draw */
    background(255);
    for (SwarmEntity s : mSwarmEntities) {
        s.draw(g);
    }
}

class SwarmEntity extends BehaviorParticle {
    
final Alignment<SwarmEntity> alignment;
    
final Cohesion<SwarmEntity> cohesion;
    
final Motor motor;
    
final Separation<SwarmEntity> separation;
    
final Wander wander;
    
SwarmEntity() {
        maximumInnerForce(random(100.0f, 1000.0f));
        radius(10f);
        separation = new Separation();
        separation.proximity(20);
        separation.weight(50.0f);
        behaviors().add(separation);
        alignment = new Alignment();
        alignment.proximity(30);
        alignment.weight(30.0f);
        behaviors().add(alignment);
        cohesion = new Cohesion();
        cohesion.proximity(100);
        cohesion.weight(5.0f);
        behaviors().add(cohesion);
        wander = new Wander();
        behaviors().add(wander);
        motor = new Motor();
        motor.auto_update_direction(true);
        motor.strength(20.0f);
        behaviors().add(motor);
    }
    
void update() {
        separation.neighbors(mSwarmEntities);
        alignment.neighbors(mSwarmEntities);
        cohesion.neighbors(mSwarmEntities);
    }
    
void draw(PGraphics g) {
        pushMatrix();
        translate(position().x, position().y, position().z);
        pushMatrix();
        PMatrix3D p = new PMatrix3D();
        teilchen.util.Util.pointAt(p, position(), new PVector(0, 1, 0), PVector.add(position(), velocity()));
        applyMatrix(p);
        noStroke();
        fill(0);
        scale(1, 0.25f, 3);
        box(6);
        popMatrix();
        /* velocity */
        stroke(0, 31);
        line(0, 0, 0, velocity().x, velocity().y, velocity().z);
        popMatrix();
    }
}
