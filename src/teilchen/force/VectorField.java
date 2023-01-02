/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2020 Dennis P Paul.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */

package teilchen.force;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import teilchen.Particle;
import teilchen.Physics;
import teilchen.util.Util;
import teilchen.util.Vector3i;

public class VectorField implements IForce {

    public static final int DISABLE_IGNORE_3D = 1;
    public static final int ENABLE_IGNORE_3D = 0;
    public VectorField(int pNumCellsWidth, int pNumCellsHeight) {
        this(pNumCellsWidth, pNumCellsHeight, 1);
    }
    public VectorField(int pNumCellsWidth, int pNumCellsHeight, int pNumCellsDepth) {
        mID = Physics.getUniqueID();
        mActiveState = true;
        mDead = false;
        width = pNumCellsWidth;
        height = pNumCellsHeight;
        depth = pNumCellsDepth;
        mField = new PVector[width][height][depth];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    mField[x][y][z] = new PVector();
                }
            }
        }
        mScale = new PVector().set(1, 1, 1);
        mPosition = new PVector();
    }

    public static void draw(PGraphics g, VectorField v, float pForceScale) {
        g.pushMatrix();
        if (v.mIgnore3D) {
            g.translate(v.position().x, v.position().y);
        } else {
            g.translate(v.position().x, v.position().y, v.position().z);
        }
        for (int x = 0; x < v.width; x++) {
            for (int y = 0; y < v.height; y++) {
                for (int z = 0; z < v.depth; z++) {
                    PVector mForce = v.cells()[x][y][z];
                    drawQuad(g, v, x, y, z);
                    drawVelocity(g, v, x, y, z, mForce, pForceScale);
                }
            }
        }
        g.popMatrix();
    }

    private static void drawQuad(PGraphics g, VectorField v, float x, float y, float z) {
        g.beginShape(PGraphics.QUAD);
        final PVector s = v.cell_size();
        x *= s.x;
        y *= s.y;
        z *= s.z;
        if (v.mIgnore3D) {
            g.vertex(x + 0, y + 0);
            g.vertex(x + s.x, y + 0);
            g.vertex(x + s.x, y + s.y);
            g.vertex(x + 0, y + s.y);
        } else {
            g.vertex(x + 0, y + 0, z);
            g.vertex(x + s.x, y + 0, z);
            g.vertex(x + s.x, y + s.y, z);
            g.vertex(x + 0, y + s.y, z);
        }
        g.endShape();
    }

    private static void drawVelocity(PGraphics g, VectorField v, int x, int y, int z, PVector p, float pForceScale) {
        final PVector s = v.cell_size();
        float x0 = (x + 0.5f) * s.x;
        float y0 = (y + 0.5f) * s.y;
        float z0 = (z + 0.5f) * s.z;
        float x1 = x0 + p.x * pForceScale;
        float y1 = y0 + p.y * pForceScale;
        float z1 = z0 + p.z * pForceScale;

        if (v.mIgnore3D) {
            g.line(x0, y0, x1, y1);
        } else {
            g.line(x0, y0, z0, x1, y1, z1);
        }
    }

    public void hint(int pFlag) {
        switch (pFlag) {
            case DISABLE_IGNORE_3D:
                mIgnore3D = false;
                break;
            case ENABLE_IGNORE_3D:
                mIgnore3D = true;
                break;
        }
    }

    public PVector[][][] cells() {
        return mField;
    }

    @Override
    public void apply(final float pDeltaTime, final Physics pParticleSystem) {
        for (final Particle mParticle : pParticleSystem.particles()) {
            if (!mParticle.fixed()) {
                final PVector mForce = getForce(mParticle);
                mParticle.force().add(mForce);
            }
        }
    }

    @Override
    public boolean dead() {
        return mDead;
    }

    public void dead(boolean pDead) {
        mDead = pDead;
    }

    @Override
    public boolean active() {
        return mActiveState;
    }

    @Override
    public void active(boolean pActiveState) {
        mActiveState = pActiveState;
    }

    public PVector cell_size() {
        return mScale;
    }

    public void randomize_forces(float pScaleWidth, float pScaleHeight) {
        randomize_forces(new PVector(pScaleWidth, pScaleHeight, 0));
    }

    public void randomize_forces(float pScaleWidth, float pScaleHeight, float pScaleDepth) {
        randomize_forces(new PVector(pScaleWidth, pScaleHeight, pScaleDepth));
    }

    public void randomize_forces(float pScale) {
        randomize_forces(new PVector(pScale, pScale, pScale));
    }

    public void randomize_forces(PVector pScale) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    final PVector p = mField[x][y][z];
                    if (mIgnore3D) {
                        Util.randomize2D(p);
                    } else {
                        Util.randomize(p);
                    }
                    Util.scale(p, pScale);
                }
            }
        }
    }

    public long ID() {
        return mID;
    }

    public void set_force_strength(float pForce) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    final PVector p = mField[x][y][z];
                    p.normalize();
                    p.mult(pForce);
                }
            }
        }
    }

    public PVector position() {
        return mPosition;
    }

    public void smooth_forces(boolean pWrap) {
        if (mIgnore3D) {
            if (width > 2 && height > 2) {
                final PVector[][][] mFieldCopy = new PVector[width][height][1];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        final int z = 0;
                        final int xP;
                        final int xN;
                        final int yP;
                        final int yN;
                        if (!pWrap) {
                            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                                continue;
                            }
                            xP = x - 1;
                            xN = x + 1;
                            yP = y - 1;
                            yN = y + 1;
                        } else {
                            xP = x == 0 ? width - 1 : x - 1;
                            xN = x == width - 1 ? 0 : x + 1;
                            yP = y == 0 ? height - 1 : y - 1;
                            yN = y == height - 1 ? 0 : y + 1;
                        }
                        final PVector p = new PVector().set(mField[x][y][z]);
                        final PVector a = mField[xP][y][z];
                        final PVector b = mField[x][yN][z];
                        final PVector c = mField[xN][y][z];
                        final PVector d = mField[x][yP][z];
                        p.add(a);
                        p.add(b);
                        p.add(c);
                        p.add(d);
                        p.div(5.0f);
                        mFieldCopy[x][y][z] = p;
                    }
                }
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        final int z = 0;
                        if (!pWrap) {
                            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                                continue;
                            }
                        }
                        mField[x][y][z].set(mFieldCopy[x][y][z]);
                    }
                }
            }
        } else {
            if (width > 2 && height > 2 && depth > 2) {
                System.out.println("@VectorField  for 3D smoothing is not implemented");
                for (int x = 1; x < width - 1; x++) {
                    for (int y = 1; y < height - 1; y++) {
                        for (int z = 1; z < depth - 1; z++) {
                            final PVector p = mField[x][y][z];
                        }
                    }
                }
            }
        }
    }

    public boolean inside(PVector p) {
        return checkLocation(getLocation(p));
    }

    private boolean checkLocation(Vector3i mLocation) {
        return mLocation.x >= 0 && mLocation.x < width && mLocation.y >= 0 && mLocation.y < height && mLocation.z >= 0 && mLocation.z < depth;
    }

    private PVector getForce(Particle p) {
        final Vector3i mLocation = getLocation(p.position());
        if (mIgnore3D) {
            mLocation.z = 0;
        } else {
            mLocation.z = PApplet.floor((p.position().z - mPosition.z) / mScale.z);
        }
        if (checkLocation(mLocation)) {
            return mField[mLocation.x][mLocation.y][mLocation.z];
        } else {
            return new PVector();
        }
    }

    private Vector3i getLocation(PVector pPosition) {
        final Vector3i mLocation = new Vector3i();
        mLocation.x = PApplet.floor((pPosition.x - mPosition.x) / mScale.x);
        mLocation.y = PApplet.floor((pPosition.y - mPosition.y) / mScale.y);
        mLocation.z = PApplet.floor((pPosition.z - mPosition.z) / mScale.z);
        return mLocation;
    }
    public boolean mIgnore3D = false;
    private final PVector[][][] mField;
    private final int width;
    private final int height;
    private final int depth;
    private final PVector mScale;
    private final PVector mPosition;
    private boolean mActiveState;
    private boolean mDead;
    private final long mID;
}
