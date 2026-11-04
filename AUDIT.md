# Teilchen Library Audit

Date: 2026-11-02

## Scope

Audited: `Physics`, particles, all integrators (`Euler`, `Midpoint`, `RungeKutta`, `Verlet`), `Spring`, `Stick`,
`Box`, `ReflectBox`, `Teleporter`, `CollisionManager`, `BasicBehaviorParticle`, and selected behaviors.

Not audited in depth: util math (`Matrix3f`, `Intersection`, `TransformMatrix4f`), `cubicle/*`, most deflectors.

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
