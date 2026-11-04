# Teilchen Library Audit

Date: 2026-11-02

## Scope

Audited: `Physics`, particles, all integrators (`Euler`, `Midpoint`, `RungeKutta`, `Verlet`), `Spring`, `Stick`,
`Box`, `ReflectBox`, `Teleporter`, `CollisionManager`, `BasicBehaviorParticle`, and selected behaviors.

Not audited in depth: `TransformMatrix4f`. `Matrix3f`, `Intersection` and `cubicle/*` were audited on 2026-11-04, see
[Second pass](#second-pass-intersection-matrix3f-cubicle). Deflectors were replaced, see
[Deflector rework](#deflector-rework--colliders-).

## Method

The library was compiled against Processing `core-4.5.7.jar`. A single particle was simulated under constant gravity
`(0, -10, 0)` for 1 s (`dt = 0.01`, initial velocity `(1, 0)`). Analytic result: `pos = (1, -5)`, `vel = (1, -10)`.

```
Euler      pos=(1.000, -4.950)  vel=(1.000, -10.000)   OK (first-order error)
Midpoint   pos=(1.500,-19.850)  vel=(1.000, -20.000)   WRONG (default integrator)
RungeKutta pos=(1.000,-12.475)  vel=(1.000, -25.000)   WRONG
Verlet     pos=(0.010, -0.100)  vel=(0.000,   0.000)   WRONG (no momentum)
Verlet (HINT_UPDATE_OLD_POSITION=false) pos=(1.000,-5.050)  OK
```

## Findings

### Critical (verified by the run above)

1. **Forces accumulate across integrator stages.** `Physics.applyForces()` adds to `Particle.force()`. Forces are
   cleared only in `Physics.handleParticles()`, after integration. `Midpoint` calls `applyForces` twice and
   `RungeKutta` calls it four times, so each stage sees the sum of all previous stages. RK4 gets
   `(1 + 2·2 + 2·3 + 4) / 6 = 2.5×` the force. Midpoint gets 2×.
2. **`Midpoint` math is wrong.** Stage one adds acceleration to `position` instead of `velocity`. Stage two continues
   from the half-step state instead of from the original state. Position overshoots by 1.5×. This is the default
   integrator.
3. **`Verlet` loses all momentum with default settings.** `Physics.postHandleParticles()` sets
   `old_position = position` after each step (static `HINT_UPDATE_OLD_POSITION` defaults to `true`). This cancels the
   `x - x_old` term, so Verlet degrades to `x += a·dt²`. The same overwrite also discards the `old_position`
   reflection that `Box` and `ReflectBox` apply under Verlet.
4. **`Physics.add(Particle, false)` and `Physics.add(Spring, false)` add nothing** but return `true`. The add happens
   only inside the `if (pPreventDuplicates)` branch.

### High

5. **`Spring` damping is per-axis.** It uses `vAB.x · n.x` per component instead of the projection `(vAB · n)`. The
   damping force is therefore not aligned with the spring axis, damps rotation, and depends on the spring's
   orientation relative to the coordinate axes.
6. **Exceptions are silently swallowed.** `applyForces`, `handleParticles`, and `postHandleParticles` catch
   `Exception` and print only when `VERBOSE` is set. A `ConcurrentModificationException` aborts half a step without
   any output.
7. **Thread safety is only apparent.** Some paths use `synchronized`, but `add`/`remove`, `Box`, and `RungeKutta`
   iterate without locks. `Util` and `Intersection` share static temporary `PVector`s (not re-entrant).
   `Physics.getUniqueID()` is not atomic.
8. **`CollisionManager` misses collisions when `HINT_IGNORE_STILL_OR_FIXED = true`.** In the non-cubicle path, pair
   `(i, j)` is checked only from particle `i` with `j ≥ i`. If `i` is still or fixed, a moving particle `j > i` is
   never tested against it.
9. **`CollisionManager` cubicle path creates every pair twice** (A→B and B→A), which doubles the springs and the
   resulting force. It also does not tag particles, unlike the non-cubicle path.
10. **`CollisionManager.removeCollisionResolver()` clears all forces and constraints** of the collision `Physics`,
    including ones added by the user.

### Medium

11. Null checks after dereference: `!p.fixed() && p != null` and `f.active() && f != null`.
12. `IntegrationUtil.checkContainerSize()` never shrinks (`for (i = 0; i < mDiff; …)` with `mDiff < 0`).
    `integration/Util.java` duplicates `IntegrationUtil` and uses deprecated `Class.newInstance()`.
13. `ReflectBox.apply()` prints its "only works with verlet" warning on every step. The `ViscousDrag` + `Verlet`
    warning fires only through `add(Force)`, not through `makeForce`, `addForces`, or `setIntegratorRef`.
14. `Teleporter` and `Box` (teleport mode) move `position` but not `old_position`, which injects a large velocity under
    Verlet. `Box` teleport snaps to the opposite edge instead of preserving the overshoot.
15. `accumulateInnerForce()` runs once per integrator stage, so stateful behaviors (for example the random walk in
    `Wander`) advance 2–4× per step with `Midpoint` or `RungeKutta`.
16. `Stick` uses `fastInverseSqrt` (≈0.2 % error). This is much larger than `EPSILON = 0.001` at typical lengths, so
    sticks never settle and jitter slightly. One-way `Stick` ignores `mDamping`.
17. Constraints move positions without updating velocities for non-Verlet integrators.
    `HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION` applies only to fixed particles. The hint name contains a typo
    (`POSTION`).

### Low / hygiene

- Allocations in hot loops: `Spring.apply` (3 `PVector`s per call), `Verlet.integrate`, `Box`, `ReflectBox`.
- `Physics.makeParticle(PVector)` aliases the caller's vector via `setPositionRef`.
- `HINT_UPDATE_OLD_POSITION` is static while the other hints are per instance.
- No automated tests or CI. `src/teilchen/test` contains visual sketches only.
- Build artifacts (`lib/teilchen.jar`, `teilchen.zip`) and the generated `processing-library/` source tree are
  committed and can drift from `src/`.

## Fix status

Verified by `teilchen.test.TestIntegrators` (all checks pass) and by an undamped spring oscillator that returns to
`x ≈ 1.000` after one period with `Midpoint`, `RungeKutta`, and `Verlet` (`Euler`: 1.031).

| #  | Status | Change |
|----|--------|--------|
| 1  | Fixed | Multi-stage integrators restore the pre-step force before each stage. `Physics.applyForces(dt, innerForces)` added. |
| 2  | Fixed | `Midpoint` rewritten: half step from saved state, full step from original state with midpoint derivatives. |
| 3  | Fixed | `Physics` no longer overwrites `old_position` of non-fixed particles when the integrator is `Verlet`. |
| 4  | Fixed | `add(Particle, false)` and `add(Spring, false)` add the element. |
| 5  | Fixed | `Spring` damping uses `(vAB · n) n`. Rewritten with scalars (no allocations). |
| 6  | Fixed | Caught exceptions are always reported to `System.err`. |
| 7  | Fixed | `getUniqueID()` uses `AtomicLong`. `step()` holds the particle-list lock for the whole step; `add`/`remove` lock their lists. Static temporaries in `Util` and `Intersection` replaced by locals. Locking contract documented on `Physics`. |
| 8  | Fixed | A pair is skipped only if both particles are still or fixed; the check moved into the pair loop. |
| 9  | Fixed | Cubicle path handles each pair once (ordered by `ID()`) and tags particles. |
| 10 | Fixed | `CollisionManager` tracks the resolvers it creates and removes only those. |
| 11 | Fixed | Null checks moved before dereference. |
| 12 | Fixed | `checkContainerSize()` shrinks correctly. `integration/Util` deprecated and delegates to `IntegrationUtil`. |
| 13 | Fixed | `ReflectBox` warns once. `ViscousDrag`/`Verlet` warning checked in all add paths and in `setIntegratorRef`. |
| 14 | Fixed | `Teleporter` and `Box` teleport shift `old_position` by the same offset. `Box` teleport wraps by box size. |
| 15 | Fixed | Inner (behavior) forces are evaluated once per step and reused in later integrator stages. |
| 16 | Fixed | `Stick` uses exact `sqrt`. One-way `Stick` applies `mDamping`. |
| 17 | Fixed | With non-Verlet integrators, `Stick` (incl. `AngleConstraintStick`, `CollisionStick`) removes relative velocity along the stick axis, `Angular` rotates velocities with positions, `LineIntersectionConstraint` stops the particle. Hint renamed to `HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION`; fixed-particle velocity is now divided by `dt` and skipped on a particle's first step (its `old_position` may be stale if the position was set after `makeParticle()`, which caused a velocity spike that tore springs at the pins in `SketchLessonX12_TearableSpring`). `Angular` debug output gated by `Physics.VERBOSE`. |
| Low | Partly | `ParticleTrail` no longer uses deprecated `Class.newInstance()`. Hot-loop allocations removed in `Spring`, `Verlet`, `Box`. `makeParticle(PVector)` copies the vector. A headless regression test was added (`teilchen.test.TestIntegrators`). Repo artifacts unchanged. |

Note: fixes 1–3 and 17 change simulation behavior. Sketches tuned against the old integrators may need re-tuning
(for example, spring constants or damping values).

API change: `Physics.HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION` was renamed to
`Physics.HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION`.

Example tuning: to compensate for the removed force doubling, spring constants in the examples were raised by the
old effective factor (×2 for `Midpoint`, ×2.5 for `RungeKutta`) while damping was kept, which makes springs slightly
snappier than before: `Lesson04`, `Lesson05`, `Lesson06`, `X00`, `X01`, `X05`, `X11`, `X12`. Library utilities
(`StableSpringQuad`, `StickMan`, `CollisionManager` defaults) were not changed.

Gravity and drag were re-tuned too. Fitting fall trajectories over 2 s against the old build showed the old `Midpoint`
behaving like ×3.5 gravity / ×2 drag, and old `RungeKutta` like ×2–3 gravity / ×2–3 drag. The examples now use:

| Example | Gravity ( old → new ) | Drag ( old → new ) |
|---|---|---|
| `Lesson01`, `Lesson02` | 30 → 100 | – |
| `Lesson06` ( RK ) | 98.1 → 200 | 0.2 → 0.1 |
| `Lesson07` ( Verlet ) | 9.81 → 100 | – |
| `Lesson09`, `Lesson10`, `Lesson11` | 50 → 175 | 0.1 → 0.2 |
| `X00` | 9.81 → 35 ( spring 20 → 35 ) | – |
| `X01` | 100 → 350 | 1 → 2 |
| `X02` | 9.81 → 35 | 0.85 → 1.7 |
| `X03` | 20 → 70 | 0.1 → 0.2 |
| `X04` ( RK ) | 20 → 60 | 0.85 → 2.5 |
| `X05` ( RK ) | 50 → 150 | 1 → 3 |
| `X09` | ( 0, −3, −30 ) → ( 0, −10, −100 ) | – |
| `X10` | 20 → 70 | – |
| `X11` | 9.81 → 35 | – |

The old `Verlet` did not build up velocity for a free particle, because `old_position` was overwritten each step.
`Lesson08` and `X12` were left alone.

Examples driven by attractors, behaviors and vector fields were fitted the same way ( `Motor` and `Attractor` paths
over 2 s ). The old `Midpoint` behaved like ×4 force / ×2.5 drag, so force parameters ( strength, weights,
`maximumInnerForce`, break force ) were scaled ×4 and drag ×2.5:

| Example | Forces ( old → new ) | Drag ( old → new ) |
|---|---|---|
| `Lesson03` | attractor 150 → 600 | 0.75 → 1.9 |
| `Lesson12` | max inner force 100 → 400 ( break radius kept at 25 ) | – |
| `Lesson13` | max inner force 100 → 400, motor 25 → 100 | 1 → 2.5 |
| `Lesson14` | vector field 40 → 160 | 0.75 → 1.9 |
| `X06` | max inner force 50–150 → 200–600, break force 12–28 → 48–112 | 0.25 → 0.6 |
| `X08` | max inner force ×4, weights 50/30/5 → 200/120/20, motor 20 → 80 | 1 → 2.5 |

`Lesson04` and `Lesson05` keep their earlier spring tuning.

## Deflector rework ( colliders )

### Findings

`TriangleDeflector` ( and `PlaneDeflector`, `SphereDeflector`, `LineDeflector2D` ) were implemented as forces that
move positions. Forces run inside the integrator, so with `Midpoint` and `RungeKutta` the deflection was applied at
intermediate states and then discarded when the integrator rebuilt the state. In addition the triangle deflector
guessed the previous position from `velocity · dt`, snapped particles moving parallel to the *infinite* plane onto
it, damped the tangential velocity with the coefficient of restitution, used a scale-dependent distance threshold,
had no tolerance at seams between triangles, and allocated ~15 `PVector`s per particle and triangle.

Measured with the `SketchLessonX09_TriangleDeflector` setup ( 500 particles, 10 s, restitution 1 ): with `Midpoint`
88 particles fell through the floor and the bounce height dropped from 240 to 12.

### New design

- `teilchen.constraint.Collider`: base class. Colliders are constraints, applied after integration. They test the
  path from `old_position()` to `position()`, so they work with all integrators and do not miss fast particles. On
  contact the penetration is mirrored ( scaled by restitution ), only the normal velocity component is reflected,
  `old_position()` is updated so `Verlet` works, and particles are kept `SKIN` away from the surface.
- `TriangleCollider`, `TriangleMeshCollider` ( uniform hash grid, consistent auto-orientation ), `PlaneCollider`,
  `SphereCollider` ( swept ), `LineCollider2D` ( swept, rounded caps ). Particle radius is respected.
- Triangle test: the path is clipped against the triangle's prism and the side is decided where the path enters
  it. A particle practically on the surface counts as being on the front side.
- `Physics` applies all colliders jointly: for each particle the earliest contact over all colliders is resolved
  first, then colliders are tested again ( up to `MAX_ITERATIONS` ). After a contact, subsequent tests start at the
  contact point ( `Physics.collisionOrigin` ). Follow-up contacts in the same step are projected, not mirrored.
- `Physics.handleNewParticles()`: for non-Verlet integrators, particles that were never stepped get
  `old_position = position`, so particles positioned after creation do not produce a bogus path from the origin.
- The `*Deflector` forces are deprecated wrappers ( `DeflectorAdapter` ) that add their collider to the constraints
  on first use and remove it when the force is removed or dead. Existing sketches keep working, now correctly.
- `Util.createTriangleColliders(...)` and `DrawLib.draw(PGraphics, TriangleCollider, ...)` added; the triangle
  deflector factories are deprecated. Examples `Lesson09`, `Lesson10`, `Lesson11`, `X09`, `X10` use colliders.

### Results

`teilchen.test.TestColliders` ( all pass ):

- X09 floor, all four integrators, single triangles and mesh: 0 particles through the floor, bounce height 225–231.
- steep random terrain ( 200 triangles, heights 0–60, particles at up to 140 units/s, gravity 100 ), all four
  integrators × restitution 0 / 0.5 / 1 × mesh / separate triangles: 0 particles below the terrain. Before the
  joint pass, the prism test and the earliest-contact rule, up to 250 of 1000 particles leaked.
- seams, fast particles ( 100000 units/s ), resting and sliding contact, radius, sphere, line, one-sided plane,
  deprecated wrapper incl. removal.
- 1000 particles × 800 triangles: separate `TriangleCollider`s 4.7 ms/step, `TriangleMeshCollider` 0.03 ms/step
  ( old `TriangleDeflector`: 15.1 ms/step ).

### Notes

- colliders are applied together at the position of the first collider in the constraint list.
- `Physics.constraints()` also contains the colliders registered by deprecated deflectors.
- triangle edges and vertices are not rounded for particles with radius; a particle overlapping only the edge of a
  free-standing triangle is not deflected. connected meshes are not affected.
- `LineCollider2D` ignores z. `PlaneCollider` is one-sided, like `PlaneDeflector`.

## Second pass: `Intersection`, `Matrix3f`, `cubicle/*`

Date: 2026-11-04. Status: **#18, #19, #21–26 are fixed, see [Second pass fix status](#second-pass-fix-status). #20 and
#27–33 are open.** The findings below describe the code before the fixes.

### Method

`src/` at `b17ef10` was compiled against Processing `core-4.5.7.jar`. `TestIntegrators` and `TestColliders` both report
`ALL PASSED`, but neither covers the code in this section. Each finding was reproduced with a throwaway headless probe
that is not committed, unless it is marked *by reading*.

Blast radius: colliders do not use `Intersection`. Its only library caller is `LineIntersectionConstraint`, whose only
user is `wip/Sketch_NonIntersectingStructures`. `Matrix3f` is reached through `TransformMatrix4f` in `CubicleWorld` and
`CubicleWorldView`. Sketches can call all of these directly, because they are public API. `X07` never rotates the world.
`Lesson` and `X` examples do not use the cubicle path of `CollisionManager`.

### High

18. **`Intersection.intersect_line_segment_plane` has the wrong sign** (`:610`). It computes `n·(p1 − origin) / n·d`,
    which is `−t`. With plane `y = 0`, the segment `y = 1 → −1` returns `null`, and the segment `y = 1 → 3`, which does
    not cross, returns `(0, 2, 0)`.
19. **`Util.calculateNormal(AB, BC, result)` destroys its input** (`Util.java:94`). `result.cross(AB, BC)` computes
    `result × AB` and stores it in `BC`. `Plane3f.updateNormal()` therefore leaves `normal = (0, 0, 0)` and overwrites
    `vectorB` with `(0, 0, 0)`. `intersectRayPlane` calls this when `normal` is `null`.
20. **`intersectRayPlane` needs a non-normalized normal** (`:207`). Its bounds test compares against `dirDotNorm`, so the
    normal must be `edge1 × edge2`. `updateNormal()` normalizes, so fixing #19 alone is not enough: a ray through the
    inside of a 10-unit triangle misses with a unit normal and hits with `(0, 0, 100)`.
21. **`intersectRayPlane` has three more defects.** The check at `:218` is `dirDotNorm < EPSILON` and should be
    `< -EPSILON`. As a result, the parallel branch is effectively dead, and a short ray from the back side misses
    (direction length `1e-6`) while the mirrored ray hits. With `quad = true`, `dirDotDiffxEdge2` has no upper bound, so
    a point at `u = 2, v = 0.5` counts as a hit. The test at `:229` uses `> 0`, so the edge `x = 0` misses while the edge
    `y = 0` hits.
22. **`LineIntersectionConstraint` teleports particles.** With padding, if `old_position` is exactly 0 or exactly 1 away
    from the intersection, the padding code is skipped and the raw offset `old_position − intersection` becomes the
    position. Measured: `(3, -1, 0)` moved to `(0, 0, 0)`, and `(3, -1, 0)` moved to `(0, 1, 0)`. Default padding is 1,
    so a particle held at the padded distance can meet the second case, subject to float equality; this was not
    reproduced in a running simulation. Separately, `intersect_line_line` (2D) never writes `z`, and the constraint
    copies its result, so a particle at `z = 5` moves to `z = 0`. Segments at different `z` also report `INTERSECTING`.
23. **`Matrix3f.transform` applies the transpose of the matrix** (`:426`). After `setZRotation(90°)`, `transform((1,0,0))`
    gives `(0, -1, 0)`, while `getXAxis()` and `TransformMatrix4f.transform` both give `(0, 1, 0)`. **`CubicleWorld`
    depends on this:** `worldposition2index` needs the inverse rotation and gets it through the transpose. Correcting
    `transform` without changing `CubicleWorld` breaks every rotated world. `(a·b).transform(v)` applies `a` first, then
    `b`, which is the opposite order to the standard `TransformMatrix4f` convention.
24. **`CubicleWorldView` draws rotated worlds with the opposite rotation.** It passes the column-major `toArray()` to the
    row-major `applyMatrix`. With a rotated world, the point drawn for cell `(3, 2, 0)` maps back to `(3, -2, 0)`. The
    same point through `TransformMatrix4f.transform` maps back correctly, so the query is right and the view is wrong.
25. **`CollisionManager.createCollisionResolvers(world)` misses pairs across cell borders** (`CollisionManager.java:225`).
    It queries only the particle's own cell. With cell size 100 and minimum distance 20, two particles 2 apart across a
    border get 0 resolvers, and two particles 9 apart in one cell get 1.
26. **Fixed `CubicleParticle`s are never put in a cell.** `isActive()` is `!fixed()`, so `handleEntity` skips them. A
    particle fixed before its first `update()` stays off-world, so a moving particle 5 away in the same cell gets 0
    resolvers. A particle fixed later stays in its old cell even when it is moved. `isActive()` is documented as a
    temporary opt-out, which does not describe fixed obstacles.

### Medium

27. `getLocalEntities(entity, rx, ry, rz)` treats off-world as cell `(-1, -1, -1)`. An entity at `(-5000, -5000, -5000)`
    with radius 1 receives the entities in cell `(0, 0, 0)`.
28. `getLocalEntities(position, radius)` returns `null` for a position outside the grid, even when cells inside the
    radius are occupied. A query at `x = -1` with radius 1 misses an entity in cell `(0, 0, 0)`.
29. `getLocalEntities` has inconsistent results. An empty cell gives an empty list from the point query and `null` from
    the radius query. The point query returns the internal live list, so `clear()` on the result removes entities from
    the cell while they stay in `entities()`.
30. `CubicleParticle.setPositionRef` has no effect. `CubicleParticle` shadows `BasicParticle.mPosition` with its own
    field and overrides `position()`, but the setter writes the base field.
31. `Intersection` is inconsistent about rays, lines and segments. `intersect_ray_sphere` is documented as both a ray and
    a segment test, but it tests the infinite line, so it hits spheres behind the origin and beyond `p2`.
    `intersectRaySpherePoints` returns points behind the origin. Of the two `intersectRayTriangle` overloads, the
    `PVector` one rejects `t ≤ 0` and the `IntersectionResult` one returns `true` with `t = -1`.
32. `Intersection` uses absolute thresholds on values that scale with input size. A ray of length `0.001` through a unit
    sphere misses in `intersect_ray_sphere`. `intersect_line_line` (3D) reports no solution for perpendicular lines of
    half-length `0.02` (`0.03` works). `intersectRayTriangle` misses a triangle with edge `3e-4` (`1e-3` works) or a
    direction of length `5e-6` on a `0.1` triangle (`1e-5` works).
33. Particles that are dead and removed from `Physics` stay in `CubicleWorld`, because nothing removes them. Queries can
    keep returning them. *By reading:* `createCollisionResolvers(world)` can then create a spring to a particle that is
    no longer simulated.

### Low / hygiene

- `CubicleWorld.add` called twice adds an entity twice: 2 in `entities()`, and a ghost entry in the off-world atom after
  `update()`.
- `CubicleWorld.remove` short-circuits. If the cell removal fails, for example because `cubicle()` was changed from
  outside, the entity stays in `entities()`.
- A `NaN` position maps to cell `(0, 0, 0)`, because `(int) NaN` is 0.
- `Matrix3f.invert` silently does nothing for a singular matrix. `setRotation` does not normalize the axis (axis length
  2 gives determinant 1.43).
- `Matrix3f.toString` prints the transpose of the row-first field naming. `TransformMatrix4f.IDENTITY` is not `final`.
  `Matrix3f` and `TransformMatrix4f` ship test code in `main`.
- *By reading:* `intersectLinePlane` prints to `System.err` on each parallel call. `intersect_line_line` (3D) assigns a
  new `PVector` to a `null` parameter, so the result is lost. Method naming mixes `intersectX` and `intersect_x`.
  `CubicleWorld` has both `entities()` and `getEntities()`, and the misspelled field `mEntites`.

### Checked and correct

- `Matrix3f`: `determinant`, `invert`, `toArray`/`set(float[])` round trip, `transpose(self)`, `setXYZRotation`
  (equals `Rx·Ry·Rz`), `setRotation` with a unit axis. *By reading:* `setXRotation`, `setYRotation` and `setZRotation`
  are the standard matrices. The `@todo` on `setYRotation` can go.
- `Intersection`: Möller–Trumbore hit point, `intersectRaySpherePoints` values, 2D `intersect_line_line` result for
  crossing segments. The `TODO` on 3D `intersect_line_line` is stale: for skew lines it returns different points, `(0,0,0)`
  and `(0,0,1)`, as it should.
- `CubicleWorld.worldposition2index` inverts `TransformMatrix4f.transform` correctly, cell lookup included.

### Second pass fix status

Verified by `teilchen.test.TestGeometry` (all checks pass; 29 of its checks failed before the fixes). `TestIntegrators`
and `TestColliders` still pass. `CubicleWorldView` was checked by recording the matrix it applies, not by rendering.

| #  | Status | Change |
|----|--------|--------|
| 18 | Fixed | `intersect_line_segment_plane` computes `n·(origin − p1) / n·d`. |
| 19 | Fixed | `Util.calculateNormal(AB, BC, result)` writes the normal to `result` and keeps its inputs. |
| 20 | Open | `intersectRayPlane` still needs `normal = edge1 × edge2`, so `updateNormal()` and a `null` normal still miss. |
| 21 | Fixed | Parallel check uses `-EPSILON`, quad mode bounds both parameters, edge `x = 0` counts as a hit. |
| 22 | Fixed | Padding is always measured from the intersection, and a distance of 0 places the particle on it. 2D `intersect_line_line` takes `z` from segment a. |
| 23 | Fixed | `Matrix3f.transform` computes `M · v`. `Matrix3f.transformInverse` added, used by `CubicleWorld`. |
| 24 | Fixed | `CubicleWorldView` passes the matrix to `applyMatrix` in row order. |
| 25 | Fixed | `CollisionManager` searches all cells within collision distance, and the off-world atom if the distance reaches past the world border. |
| 26 | Fixed | `CubicleParticle.isActive()` is always `true`, so fixed particles are kept in the right cell. |

Note: after #21, a ray with `|direction · normal| ≤ EPSILON` counts as parallel from both sides. A very short ray from
the front used to hit and now misses, which is part of #32.

API change: `Matrix3f.transform` now rotates in the opposite direction, and `(a·b).transform(v)` applies `b` first.
Sketches that relied on the old result can use `transformInverse` for pure rotations.

### Remaining

- #20 and #31 need a decision about the intended API.
- #27–#30, #32 and #33 are open. Colliding through `CollisionManager` no longer goes through #27–#29, but direct calls
  to `getLocalEntities` still do.
- Low / hygiene items above are open.
