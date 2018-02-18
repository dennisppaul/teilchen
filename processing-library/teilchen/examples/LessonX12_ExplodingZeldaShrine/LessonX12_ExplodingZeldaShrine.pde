import teilchen.*; 
import teilchen.behavior.*; 
import teilchen.constraint.*; 
import teilchen.cubicle.*; 
import teilchen.force.*; 
import teilchen.integration.*; 
import teilchen.util.*; 


PVector p1 = new PVector(0, 0);
PVector p2 = new PVector(300, 300);
void settings() {
    size(640, 480, P3D);
}
void setup() {
    p1.set(width / 2, height / 2);
}
void draw() {
    background(0);
    noStroke();
    p2.set(mouseX, mouseY);
    laser_line(p1, p2);
}
void laser_line(PVector v1, PVector v2) {
    laser_line(v1, v2, 2, 10, color(255, 0, 0), color(255, 0, 0, 127), color(255, 0, 0, 0));
}
void laser_line(PVector v1,
                        PVector v2,
                        float pLineWidth,
                        float pFadeScale,
                        int pColorCore,
                        int pColorFadeInner,
                        int pColorFadeOuter) {
    PVector d = PVector.sub(v2, v1);
    PVector c = new PVector(-d.y, d.x);
    c.normalize();
    c.mult(pLineWidth / 2);
    beginShape(QUADS);
    /* core */
    fill(pColorCore);
    vertex(v1.x + c.x, v1.y + c.y);
    fill(pColorCore);
    vertex(v2.x + c.x, v2.y + c.y);
    fill(pColorCore);
    vertex(v2.x - c.x, v2.y - c.y);
    fill(pColorCore);
    vertex(v1.x - c.x, v1.y - c.y);
    /* top */
    fill(pColorFadeOuter);
    vertex(v1.x + c.x * pFadeScale, v1.y + c.y * pFadeScale);
    fill(pColorFadeOuter);
    vertex(v2.x + c.x * pFadeScale, v2.y + c.y * pFadeScale);
    fill(pColorFadeInner);
    vertex(v2.x + c.x, v2.y + c.y);
    fill(pColorFadeInner);
    vertex(v1.x + c.x, v1.y + c.y);
    /* bottom */
    fill(pColorFadeOuter);
    vertex(v1.x - c.x * pFadeScale, v1.y - c.y * pFadeScale);
    fill(pColorFadeOuter);
    vertex(v2.x - c.x * pFadeScale, v2.y - c.y * pFadeScale);
    fill(pColorFadeInner);
    vertex(v2.x - c.x, v2.y - c.y);
    fill(pColorFadeInner);
    vertex(v1.x - c.x, v1.y - c.y);
    endShape();
}
