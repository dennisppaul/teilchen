# teilchen

![LessonX04_StickMan](https://raw.githubusercontent.com/d3p/teilchen/master/img/LessonX04_StickMan.png)

- *teilchen* is a simple physics library based on particles, forces, constraints and behaviors. 
- *teilchen* is also a collection of a variety of concepts useful for modeling with virtual physics and behaviors. nothing new nothing fancy, except maybe for the combination of forces ( *external forces* ) and behavior ( *internal forces* ).
- *teilchen* is also a [processing.org](http://processing.org "Processing.org")-style library.
- *teilchen* is a german word and a synonym for *Partikel* which translates to the english *particle*.

## anatomy of a physic-based particle system

### particles

there are different kinds of particles. the most simple particle has just a handful of properties like *position*, *velocity*, *acceleration*, *mass*, and *radius*. other particles might have properties like a limited lifetime or individual behaviors.

### forces

forces act on particles ( or rather on their acceleration ). one of the most obvious force is `Gravity` which pulls particles into a specific direction, but there all kinds of other forces like `Attractor` or `TriangleDeflector`. some forces affect on all particles in a system, while others only act on specific particles. one prominent example of the latter is the `Spring` that tries to maintain a fixed distance between two particles.

### behaviors

a *behavioris a special kind of force. it is an internal force like for example a motor that only affects a single particle. a typical force is for example the `Seek` behavior which constantly pulls a particle towards a certain point.

### constraints

constraints act on particle positions outside of a physical simulation. constraints manipulate the particle’s position to achieve satifsy specific constraints like for example keeping an equal distance between two particles or keeping particles within a volume. although this behavior might seem similar to that of forces it is important to know that forces can only *accelerate* particles while contraints directly *teleport* particles which as a rule of thumb is more effective but less realistic.

### integrators

integrators are used to integrate acceleration and velocity to calculate the new position. the most well-known is the *euler* integrator, but there are also optimized versions like *runge-kutta* or *midpoint* or even slightly different concepts like *verlet*.
