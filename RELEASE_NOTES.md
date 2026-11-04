# teilchen / release notes

## v0.5 (20261104)

- fixed: `Midpoint` and `RungeKutta` counted forces several times per step. motion with these integrators is now physically correct but noticeably slower than before. *breaking:* sketches tuned for v0.4 need about 3× gravity and 2–2.5× drag ( Midpoint ), or 4× attractor/behavior forces and 2.5× drag. see `AUDIT.md` for the factors used in the examples
- fixed: `Verlet` now builds up velocity for free particles ( `old_position` was overwritten every step )
- fixed: behavior ( inner ) forces are evaluated once per step and reused in later integrator stages
- fixed: `Spring` damping acts along the spring axis only. `Stick` uses an exact square root and removes relative velocity along its axis with non-Verlet integrators. `Angular` rotates velocities with positions
- fixed: `add(Particle, false)` and `add(Spring, false)` did not add the element
- fixed: `Box` and `Teleporter` wrap `old_position` together with the position
- fixed: `CollisionManager` pair handling ( duplicate pairs, removal of foreign resolvers )
- fixed: pinned ( fixed ) particles no longer get a velocity spike on their first step ( tore springs in `LessonX12_TearableSpring` )
- added: swept colliders `TriangleCollider`, `TriangleMeshCollider` ( spatial hash, fast for big meshes ), `PlaneCollider`, `SphereCollider` and `LineCollider2D`. fast particles no longer tunnel through, particle radius is respected, and all colliders are resolved together by earliest contact
- added: `SphereDeflector`, refined triangle deflection
- added: `Physics.stepCount()`, `Util.createTriangleColliders(...)`, `DrawLib.draw(PGraphics, TriangleCollider, ...)`
- added: entity IDs ( thread-safe )
- added: headless tests `teilchen.test.TestIntegrators` and `teilchen.test.TestColliders`
- changed: `Physics` is safer to use from multiple threads ( locking contract documented on `Physics` )
- changed: hint `HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION` renamed to `HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION` ( typo fix, *breaking* )
- deprecated: `TriangleDeflector`, `PlaneDeflector`, `SphereDeflector`, `LineDeflector2D` and the other `*Deflector` forces. they still work and now use the new colliders internally
- deprecated: `teilchen.integration.Util` ( use `IntegrationUtil` )
- changed: all examples use colliders and are re-tuned to the corrected integrators
- added: `dist/release.sh` builds, tests and publishes a release from the command line ( no IDE needed )

## v0.4 (20200616)

- particles, contraints and forces can now be marked `dead` and are automatically disposed when calling `Physics.step()` or `Physics.purge()`
- added example for `MuscleSpring`
- added example for `TearableSpring`
- added example for `VectorField`
- added *anatomy of a particle* tutorial

## v0.3 (20200528)

- prepared library to be published on the processing.org website

## v0.2 (20160423)

- updated for processing3
- now using PVector for linear algebra
- removed dependency on *mathematik*

## v0.1 (20150228)

- initial release
