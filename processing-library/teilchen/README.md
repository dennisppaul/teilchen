# teilchen

![teilchen showreel](https://raw.githubusercontent.com/dennisppaul/teilchen/master/resources/teilchen-showreel.gif)

- *teilchen* is a simple physics library based on particles, forces, constraints and behaviors. 
- *teilchen* is also a collection of a variety of concepts useful for modeling with virtual physics and behaviors. nothing new, nothing fancy, except maybe for the combination of forces ( *external forces* ) and behavior ( *internal forces* ).
- *teilchen* is also a [processing.org](http://processing.org "Processing.org")-style library.
- *teilchen* is a german word and a synonym for *Partikel* which translates to the english *particle*.

the library is hosted on github [teilchen](https://github.com/dennisppaul/teilchen).

## anatomy of a physic-based particle system

### particles

there are a few different kinds of particles. the most simple particle has just a handful of properties like *position*, *velocity*, *acceleration*, *mass*, and *radius*. other particles have additional properties like a limited lifetime or individual behaviors.

### forces

forces act on particles ( or rather on their acceleration ). one of the most obvious forces is `Gravity` which pulls particles into a specific direction. but there are all kinds of other forces too like `Attractor` or `TriangleDeflector`. some forces affect all particles in a system, while others only act on specific particles. one prominent example of the latter is the `Spring` that tries to maintain a fixed distance between two particles.

### behaviors

a *behavior* is a special kind of force. it is an *internal* force moving the particle from *within* and it affects a single particle only. an common example of a behavior is a `Motor` which drives a particle into a specific direction. another example is the `Seek` behavior which constantly drives a particle towards a certain position.

### constraints

constraints act on particle positions outside of a physical simulation. constraints manipulate the particles’ positions to satifsy specific constraints like for example keeping an equal distance between two particles or keeping particles within a volume. although constraints might seem similar to forces, it is important to know that forces can only *accelerate* particles while contraints directly *teleport* particles which as a rule of thumb is more *effective* but less *realistic*.

### integrators

integrators are used to integrate acceleration and velocity to calculate the new position. the most well-known is the *euler* integrator, but there are also optimized versions like *runge-kutta* or *midpoint* or even slightly different concepts like *verlet*. integrators can affect the precision and stability of a particle simulation.

## anatomy of a particle

![](./resources/teilchen-tutorial-particles-01.png)

![](./resources/teilchen-tutorial-particles-02.png)

![](./resources/teilchen-tutorial-particles-03.png)

![](./resources/teilchen-tutorial-particles-04.png)

## moving a particle ( with forces )

![](./resources/teilchen-tutorial-particles-05.png)

![](./resources/teilchen-tutorial-particles-06.png)

![](./resources/teilchen-tutorial-particles-07.png)

![](./resources/teilchen-tutorial-particles-08.png)

![](./resources/teilchen-tutorial-particles-09.png)

![](./resources/teilchen-tutorial-particles-10.png)

![](./resources/teilchen-tutorial-particles-11.png)

![](./resources/teilchen-tutorial-particles-12.png)

## reference

generated [reference](http://dennisppaul.github.io/teilchen/) of the library

## vectors

although explaining vectors ( and linear algebra ) is beyond the scope of this library, it can be very helpful to understand the basics of vector operations. the following slides aim to explain a few basics:

### anatomy of a vector

![](./resources/teilchen-tutorial-vectors-01.png)

a vector consists of a series of numbers ( one for each dimension ) that are the coordinates of the endpoint of that vector. therefore vectors are often used to describe positions and directions.

this example shows a 2-dimensional vector pointing to the x-coordinate 4 and the y-coordinate 3. the vector is written as: `(4,3)`

visually speaking a vector is like an arrow starting at the origin (0,0) and pointing to an endpoint ( e.g `(4,3)` ).

![](./resources/teilchen-tutorial-vectors-02.png)

in programm code a vector can be represented as a class with two fields x + y:

    class Vector2f {
        float x = 0;
        float y = 0;
    }

    Vector2f a = new Vector2f(4,3);
    
    println("(" + a.x + "," + a.y + ")");

    > (4,3)

### adding vectors

![](./resources/teilchen-tutorial-vectors-03.png)

vectors can be added by adding the components of one vector to the components of another one.

in this example the x-components `4` + `2` are added with the result of `6` and the y-components `3` + `-1` are added resulting in `2`. 

    (4,3) + (2,-1) = (6,2)

visually speaking vectors are added by putting the start of one vector at the end of another. the result is a vector running from the start of the first vector to the end of the second.

![](./resources/teilchen-tutorial-vectors-04.png)

in programm code an addition of one vector to another vector can be represented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) {
            x += v.x;
            y += v.y;
        }
    }
    
    Vector2f a = new Vector2f(4,3);
    Vector2f b = new Vector2f(2,-1);
    
    a.add(b);
    println("(" + a.x + "," + a.y + ")");
    
    > (6,2)

### subtracting vectors

![](./resources/teilchen-tutorial-vectors-05.png)

vectors can be subtracted by subtracting the components of one vector from the components of another vector.

in this example the x-components `4` is subtracted from `6` with the result of `2` and the y-component `3` is subtracted from `2` resulting in `-1`. 

    (6,2) - (4,3) = (2,-1)

visually speaking the result of a subtraction is a vector between the tips of the two vectors. the resulting vector points to the tip of the first vector away from the second vector.

![](./resources/teilchen-tutorial-vectors-06.png)

in programm code a subtraction of a vector from another vector can be represented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) {
            x += v.x;
            y += v.y;
        }
    
        void sub(Vector2f v) {
            x -= v.x;
            y -= v.y;
        }
    }
    
    Vector2f a = new Vector2f(6,2);
    Vector2f b = new Vector2f(4,3);
    
    a.sub(b);
    println("(" + a.x + "," + a.y + ")");
    
    > (2,-1)

### scaling vectors

![](./resources/teilchen-tutorial-vectors-07.png)

a vector can be scaled ( or multiplied ) by a value ( or scalar ).

in this example the vector `(4,3)` is scaled by a value of `2` resulting in the vector `(8,6)`.

    (4,3) * 2 = (8,6)

visually speaking scaling a vector changes the length of the vector.

![](./resources/teilchen-tutorial-vectors-08.png)

in programm code scaling a vector can be represented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) {
            x += v.x;
            y += v.y;
        }
    
        void sub(Vector2f v) {
            x -= v.x;
            y -= v.y;
        }
    
        void mult(float s) {
            x *= s;
            y *= s;
        }
    }
    
    Vector2f a = new Vector2f(4,3);
    
    a.mult(2);
    println("(" + a.x + "," + a.y + ")");
    
    > (8,6)

### length of a vector

![](./resources/teilchen-tutorial-vectors-09.png)

the length ( or magnitude ) of a vector can be calculated by using the pythagorean theorem:

    a * a + b * b = c * c

in this example the length `5` of the vector `(4,3)` is calculated where a = x, b = y, and c = length:

    x * x + y * y      = length * length
    length * length    = 4 * 4 + 3 * 3
    length             = √( 4 * 4 + 3 * 3 )
    length             = 5

![](./resources/teilchen-tutorial-vectors-10.png)

in programm code calculating the length of a vector can be implemented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) {
            x += v.x;
            y += v.y;
        }
    
        void sub(Vector2f v) {
            x -= v.x;
            y -= v.y;
        }
    
        void mult(float s) {
            x *= s;
            y *= s;
        }
    
        float mag() {
            return sqrt(x*x + y*y);
        }
    }
    
    Vector2f a = new Vector2f(4,3);
    
    float length = a.mag();
    println(length);
    
    > 5

### normalizing a vector

![](./resources/teilchen-tutorial-vectors-11.png)

normalizing a vector refers to setting its magnitude to a value of 1. this is achieved by dividing each component of a vector by the initial magnitude.

in this example the length of the vector `(4,3)` is `5`:

    (4/5,3/5) = (0.8,0.6)
    
![](./resources/teilchen-tutorial-vectors-12.png)

in programm code normalizing a vector can be represented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) { … }
    
        void sub(Vector2f v) { … }
    
        void mult(float s) { … }
    
        float mag() {
            return sqrt(x*x + y*y);
        }
    
        void normalize() {
            float d = mag();
            x /= d;
            y /= d;
        }
    }
    
    Vector2f a = new Vector2f(4,3);
    a.normalize();
    
    println("(" + a.x + "," + a.y + ") " + a.mag());
    
    > (0.8,0.6) 1.0

### a 2D vector class

![](./resources/teilchen-tutorial-vectors-13.png)

note, that this class is implementing just a very basic set of functions. a *real life* vector class ( i.e `PVector` from Processing.org ) has many, many more functions. in programm code a 2-dimensional vector class can be represented like this:

    class Vector2f {
        float x = 0;
        float y = 0;
                    
        void add(Vector2f v) {
            x += v.x;
            y += v.y;
        }
    
        void sub(Vector2f v) {
            x -= v.x;
            y -= v.y;
        }
    
        void mult(float s) {
            x *= s;
            y *= s;
        }
    
        float mag() {
            return sqrt(x*x + y*y);
        }
    
        void normalize() {
            float d = mag();
            x /= d;
            y /= d;
        }
    }

### a 3D vector class

![](./resources/teilchen-tutorial-vectors-14.png)

a 3-dimensional vector is not that different from a 2-dimensional vector. in many operations it suffices to add a z-coordinate. in programm code it can be represented like this:

    class Vector3f {
        float x = 0;
        float y = 0;
        float z = 0;
                    
        void add(Vector3f v) {
            x += v.x;
            y += v.y;
            z += z.y;
        }
    
        void sub(Vector3f v) {
            x -= v.x;
            y -= v.y;
            z -= v.z;
        }
    
        void mult(float s) {
            x *= s;
            y *= s;
            z *= s;
        }
    
        float mag() {
            return sqrt(x*x + y*y + z*z);
        }
    
        void normalize() {
            float d = mag();
            x /= d;
            y /= d;
            z /= d;
        }
    }

note, that this class is implementing just a very basic set of functions. a *real life* vector class ( i.e `PVector` from Processing.org ) has many, many more functions.

### further readings

[J. Ström, K. Åström, and T. Akenine-Möller: immersive linear algebra](http://immersivemath.com/ila/) sheds a much more *mathematical* light on the topic … but has animated illustrations and takes some time to introduces the *official* terminology.

a good starting point and a great resource for learning more about vectors, especially in combination with matrices, is [The Matrix and Quaternions FAQ ](https://cxc.cfa.harvard.edu/mta/ASPECT/matrix_quat_faq/). 
