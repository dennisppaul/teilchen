/*
 * Teilchen
 *
 * This file is part of the *teilchen* library (https://github.com/dennisppaul/teilchen).
 * Copyright (c) 2024 Dennis P Paul.
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

package teilchen.constraint;

import processing.core.PVector;
import teilchen.Particle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * keeps particles from passing through a mesh of triangles. triangles are two-sided and stored in a uniform spatial
 * hash grid so that each particle is only tested against triangles near its path.
 * <p>
 * vertices are copied when the collider is created. if the mesh changes call <code>update(float[])</code> ( or
 * <code>update()</code> for colliders created from an indexed vertex array, which is referenced ).
 */
public class TriangleMeshCollider extends Collider {

    /**
     * if true triangle winding is made consistent across shared edges when the mesh is built. particles that are
     * exactly on the surface count as being on the front side, so consistent winding gives consistent behavior. set
     * before calling <code>update</code>.
     */
    public boolean AUTO_ORIENT = true;

    /* if a particle path covers more cells than this, all triangles are tested instead */
    private static final int MAX_CELLS_PER_QUERY = 512;

    private ColliderTriangle[] mTriangles = new ColliderTriangle[0];
    private boolean[] mTriangleHit = new boolean[0];
    private int[] mTriangleStamp = new int[0];
    private int mStamp = 0;
    private int mBest = -1;
    private int mResolving = -1;
    private final HashMap<Long, int[]> mGrid = new HashMap<>();
    private float mCellSize;
    private float mRequestedCellSize;
    private final float[] mIndexedVertices;
    private final int[] mIndices;

    /**
     * @param pVertices triangle soup with 9 floats ( 3 vertices ) per triangle
     */
    public TriangleMeshCollider(float[] pVertices) {
        this(pVertices, 0);
    }

    /**
     * @param pVertices triangle soup with 9 floats ( 3 vertices ) per triangle
     * @param pCellSize size of grid cells. if 0 a size is derived from the triangles.
     */
    public TriangleMeshCollider(float[] pVertices, float pCellSize) {
        mIndexedVertices = null;
        mIndices = null;
        mRequestedCellSize = pCellSize;
        update(pVertices);
    }

    /**
     * @param pVertices triangle soup with 3 vertices per triangle
     */
    public TriangleMeshCollider(PVector[] pVertices) {
        this(toArray(pVertices), 0);
    }

    /**
     * creates a mesh collider from an indexed vertex array. the vertex array is referenced, call
     * <code>update()</code> after vertices changed.
     *
     * @param pVertices vertex array with 3 floats per vertex
     * @param pIndices  3 vertex indices per triangle
     * @param pCellSize size of grid cells. if 0 a size is derived from the triangles.
     */
    public TriangleMeshCollider(float[] pVertices, int[] pIndices, float pCellSize) {
        mIndexedVertices = pVertices;
        mIndices = pIndices;
        mRequestedCellSize = pCellSize;
        update();
    }

    private static float[] toArray(PVector[] pVertices) {
        final float[] mVertices = new float[pVertices.length * 3];
        for (int i = 0; i < pVertices.length; i++) {
            mVertices[i * 3] = pVertices[i].x;
            mVertices[i * 3 + 1] = pVertices[i].y;
            mVertices[i * 3 + 2] = pVertices[i].z;
        }
        return mVertices;
    }

    /**
     * rebuilds mesh from referenced indexed vertex array.
     */
    public void update() {
        if (mIndexedVertices == null) {
            rebuildGrid();
            return;
        }
        final float[] mVertices = new float[mIndices.length * 3];
        for (int i = 0; i < mIndices.length; i++) {
            final int j = mIndices[i] * 3;
            mVertices[i * 3] = mIndexedVertices[j];
            mVertices[i * 3 + 1] = mIndexedVertices[j + 1];
            mVertices[i * 3 + 2] = mIndexedVertices[j + 2];
        }
        update(mVertices);
    }

    /**
     * rebuilds mesh from triangle soup with 9 floats per triangle.
     */
    public void update(float[] pVertices) {
        final int mCount = pVertices.length / 9;
        if (mTriangles.length != mCount) {
            mTriangles = new ColliderTriangle[mCount];
            for (int i = 0; i < mCount; i++) {
                mTriangles[i] = new ColliderTriangle();
            }
            mTriangleHit = new boolean[mCount];
            mTriangleStamp = new int[mCount];
        }
        final boolean[] mFlip = AUTO_ORIENT ? orient(pVertices, mCount) : new boolean[mCount];
        for (int i = 0; i < mCount; i++) {
            final int j = i * 9;
            if (mFlip[i]) {
                /* swap b and c */
                mTriangles[i].set(pVertices[j], pVertices[j + 1], pVertices[j + 2],
                                  pVertices[j + 6], pVertices[j + 7], pVertices[j + 8],
                                  pVertices[j + 3], pVertices[j + 4], pVertices[j + 5]);
            } else {
                mTriangles[i].set(pVertices[j], pVertices[j + 1], pVertices[j + 2],
                                  pVertices[j + 3], pVertices[j + 4], pVertices[j + 5],
                                  pVertices[j + 6], pVertices[j + 7], pVertices[j + 8]);
            }
        }
        rebuildGrid();
    }

    /**
     * makes winding of triangles consistent across shared edges ( flood fill ). the first triangle of each
     * connected part keeps its winding. triangles sharing an edge must use identical vertex coordinates.
     *
     * @return per triangle true if winding must be flipped
     */
    private static boolean[] orient(float[] pVertices, int pCount) {
        /* map undirected edge to triangles and direction */
        final HashMap<String, ArrayList<int[]>> mEdges = new HashMap<>();
        for (int i = 0; i < pCount; i++) {
            for (int e = 0; e < 3; e++) {
                final String p = vertexKey(pVertices, i * 9 + e * 3);
                final String q = vertexKey(pVertices, i * 9 + ((e + 1) % 3) * 3);
                final boolean mForward = p.compareTo(q) < 0;
                final String k = mForward ? p + "|" + q : q + "|" + p;
                mEdges.computeIfAbsent(k, x -> new ArrayList<>()).add(new int[]{i, mForward ? 1 : -1});
            }
        }
        final ArrayList<ArrayList<int[]>> mNeighbors = new ArrayList<>();
        for (int i = 0; i < pCount; i++) {
            mNeighbors.add(new ArrayList<>());
        }
        for (ArrayList<int[]> l : mEdges.values()) {
            for (int[] a : l) {
                for (int[] b : l) {
                    if (a[0] != b[0]) {
                        /* consistent winding traverses a shared edge in opposite directions */
                        mNeighbors.get(a[0]).add(new int[]{b[0], a[1] == b[1] ? 1 : 0});
                    }
                }
            }
        }
        final boolean[] mFlip = new boolean[pCount];
        final boolean[] mVisited = new boolean[pCount];
        final java.util.ArrayDeque<Integer> mQueue = new java.util.ArrayDeque<>();
        for (int s = 0; s < pCount; s++) {
            if (mVisited[s]) {
                continue;
            }
            mVisited[s] = true;
            mQueue.add(s);
            while (!mQueue.isEmpty()) {
                final int i = mQueue.poll();
                for (int[] n : mNeighbors.get(i)) {
                    if (!mVisited[n[0]]) {
                        mVisited[n[0]] = true;
                        /* same direction on shared edge means inconsistent winding */
                        mFlip[n[0]] = mFlip[i] ^ (n[1] == 1);
                        mQueue.add(n[0]);
                    }
                }
            }
        }
        return mFlip;
    }

    private static String vertexKey(float[] v, int i) {
        return Float.floatToIntBits(v[i]) + "," + Float.floatToIntBits(v[i + 1]) + "," + Float.floatToIntBits(v[i + 2]);
    }

    public void cellsize(float pCellSize) {
        mRequestedCellSize = pCellSize;
        rebuildGrid();
    }

    public float cellsize() {
        return mCellSize;
    }

    public int triangles() {
        return mTriangles.length;
    }

    /**
     * @return true if triangle was hit during the last step
     */
    public boolean hit(int pTriangle) {
        return mTriangleHit[pTriangle];
    }

    private void rebuildGrid() {
        mGrid.clear();
        if (mTriangles.length == 0) {
            mCellSize = 1;
            return;
        }
        if (mRequestedCellSize > 0) {
            mCellSize = mRequestedCellSize;
        } else {
            /* average of largest triangle extent */
            double mSum = 0;
            for (ColliderTriangle t : mTriangles) {
                mSum += Math.max(t.maxX - t.minX, Math.max(t.maxY - t.minY, t.maxZ - t.minZ));
            }
            mCellSize = (float) Math.max(mSum / mTriangles.length, 1.0e-3);
        }
        final HashMap<Long, ArrayList<Integer>> mCells = new HashMap<>();
        for (int i = 0; i < mTriangles.length; i++) {
            final ColliderTriangle t = mTriangles[i];
            if (!t.valid) {
                continue;
            }
            final int x0 = cell(t.minX), x1 = cell(t.maxX);
            final int y0 = cell(t.minY), y1 = cell(t.maxY);
            final int z0 = cell(t.minZ), z1 = cell(t.maxZ);
            for (int x = x0; x <= x1; x++) {
                for (int y = y0; y <= y1; y++) {
                    for (int z = z0; z <= z1; z++) {
                        mCells.computeIfAbsent(key(x, y, z), k -> new ArrayList<>()).add(i);
                    }
                }
            }
        }
        for (java.util.Map.Entry<Long, ArrayList<Integer>> e : mCells.entrySet()) {
            final ArrayList<Integer> l = e.getValue();
            final int[] a = new int[l.size()];
            for (int i = 0; i < a.length; i++) {
                a[i] = l.get(i);
            }
            mGrid.put(e.getKey(), a);
        }
    }

    private int cell(float v) {
        return (int) Math.floor(v / mCellSize);
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x1FFFFF) << 42) | ((long) (y & 0x1FFFFF) << 21) | (long) (z & 0x1FFFFF);
    }

    protected void prepare() {
        java.util.Arrays.fill(mTriangleHit, false);
    }

    protected boolean collide(Particle pParticle) {
        final PVector p0 = pathStart(pParticle);
        final PVector p1 = pParticle.position();
        final float r = pParticle.radius() + SKIN;
        final int x0 = cell(Math.min(p0.x, p1.x) - r), x1 = cell(Math.max(p0.x, p1.x) + r);
        final int y0 = cell(Math.min(p0.y, p1.y) - r), y1 = cell(Math.max(p0.y, p1.y) + r);
        final int z0 = cell(Math.min(p0.z, p1.z) - r), z1 = cell(Math.max(p0.z, p1.z) + r);

        /* new stamp per particle to test each triangle only once */
        mStamp++;
        if (mStamp == Integer.MAX_VALUE) {
            mStamp = 1;
            java.util.Arrays.fill(mTriangleStamp, 0);
        }

        /* find earliest contact along path. resolving a later one first could push particle to wrong side. */
        mBest = -1;
        final long mCells = (long) (x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1);
        if (mCells > MAX_CELLS_PER_QUERY) {
            for (int i = 0; i < mTriangles.length; i++) {
                test(i, pParticle);
            }
        } else {
            for (int x = x0; x <= x1; x++) {
                for (int y = y0; y <= y1; y++) {
                    for (int z = z0; z <= z1; z++) {
                        final int[] mCell = mGrid.get(key(x, y, z));
                        if (mCell != null) {
                            for (int i : mCell) {
                                test(i, pParticle);
                            }
                        }
                    }
                }
            }
        }
        if (mBest < 0) {
            return false;
        }
        mResolving = mBest;
        mTriangles[mBest].resolve(pParticle, this);
        return true;
    }

    protected void resolved(Particle pParticle) {
        if (mResolving >= 0) {
            mTriangleHit[mResolving] = true;
        }
    }

    private void test(int pIndex, Particle pParticle) {
        if (mTriangleStamp[pIndex] == mStamp) {
            return;
        }
        mTriangleStamp[pIndex] = mStamp;
        final ColliderTriangle t = mTriangles[pIndex];
        if (t.test(pParticle, this) && (mBest < 0 || t.hitT < mTriangles[mBest].hitT)) {
            mBest = pIndex;
        }
    }
}
