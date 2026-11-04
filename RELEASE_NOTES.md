# teilchen / release notes

## v0.6 (unreleased)

### fixed

- *breaking* `Matrix3f.transform` applied the transpose of the matrix, so rotations ran in the opposite direction. it now computes `M * v`, and `a.multiply(b)` followed by `a.transform(v)` applies b first. added `Matrix3f.transformInverse`
- `CollisionManager.createCollisionResolvers(CubicleWorld)` missed pairs across cell borders and pairs with fixed particles. this creates more collisions than before
- `CubicleWorldView` drew rotated worlds with the opposite rotation
- `Intersection.intersect_line_segment_plane` had the wrong sign: it missed real crossings and reported false ones
- `Util.calculateNormal(AB, BC, result)` and `Plane3f.updateNormal` produced a zero normal and overwrote an input
- `Intersection.intersectRayPlane` parallel check, quad mode and triangle edges
- `LineIntersectionConstraint` could move particles towards the origin and set z to 0
- *breaking* `CubicleWorld.getLocalEntities` always returns a new list and never `null`. queries outside of the world find cells within their radius
- *breaking* `intersect_ray_sphere`, `intersectRaySpherePoints` and `intersectRayTriangle` with `IntersectionResult` no longer report hits behind the ray origin
- `Intersection.intersectRayPlane` works without a normal and ignores `Plane3f.normal`
- parallel checks in `Intersection` no longer depend on the size of the input, so small triangles and short rays are detected
- `CubicleParticle.setPositionRef` had no effect
- `CubicleWorld.update` removes dead particles
- `TransformMatrix4f.multiply` and `determinant` returned `NaN` for a non-finite translation. `TransformMatrix4f.IDENTITY` is now `final`

## v0.5 (20261104)

### fixed

- *breaking* corrected integrators: some forces need to be tuned compared to v0.4. e.g 3× gravity and 2–2.5× drag
- `Spring` damping acts along the spring axis only. 
- `Stick` uses an exact square root and removes relative velocity along its axis with non-Verlet integrators. 
- `add(Particle, false)` and `add(Spring, false)` did not add the element
- `Box` and `Teleporter` wrap `old_position` together with the position
- `CollisionManager` pair handling; duplicate pairs, removal of other resolvers
- pinned ( fixed ) particles no longer get a velocity spike on their first step
- *breaking* fixed typo in `HINT_SET_VELOCITY_FROM_PREVIOUS_POSTION` renamed to `HINT_SET_VELOCITY_FROM_PREVIOUS_POSITION`

### added

- added colliders: `TriangleCollider`, `TriangleMeshCollider` ( fast for big meshes ), `PlaneCollider`, `SphereCollider` and `LineCollider2D`. fast particles no longer tunnel through, particle radius is respected, and all colliders are resolved together by earliest contact
- added entity IDs ( thread-safe )
- made `Physics` safer to use from multiple threads

### deprecated

- `TriangleDeflector`, `PlaneDeflector`, `SphereDeflector`, `LineDeflector2D` and the other `*Deflector` forces. internally they still use the new colliders.
- `teilchen.integration.Util` ( use `IntegrationUtil` )
- all examples use colliders and are re-tuned to the corrected integrators

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
