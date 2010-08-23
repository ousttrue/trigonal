package trigonal.geometry

/**
 * geometry
 */
class Vector2(val x :Float, val y :Float)
object Vector2 {
    def apply(x :Float, y :Float) :Vector2=new Vector2(x, y)
    def apply(
            x :{def toFloat:Float}, 
            y :{def toFloat:Float}
            ) :Vector2=new Vector2(x toFloat, y toFloat)
}

class Vector3(val x :Float, val y :Float, val z :Float)
object Vector3 {
    def apply(x :Float, y :Float, z :Float) :Vector3=new Vector3(x, y, z)
    def apply(
            x :Int,
            y :Int,
            z :Int
            ) =new Vector3(
                x.toString toFloat, 
                y.toString toFloat, 
                z.toString toFloat)
    def apply(
            x :{def toFloat:Float}, 
            y :{def toFloat:Float}, 
            z :{def toFloat:Float}
            ) :Vector3=new Vector3(x toFloat, y toFloat, z toFloat)
}

/**
 * color
 */
class RGBA(val r :Float, val g :Float, val b :Float, val a :Float){
    override def toString="<RGBA %f:%f:%f:%f>".format(r, g, b, a)
}
object RGBA {
    def apply(r :Float, g :Float, b :Float, a :Float) :RGBA=new RGBA(r, g, b, a)
    def apply(
            r :{def toFloat:Float}, 
            g :{def toFloat:Float}, 
            b :{def toFloat:Float},
            a :{def toFloat:Float}
            ) :RGBA=new RGBA(r toFloat, g toFloat, b toFloat, a toFloat)
}

/**
 * Vertex
 */
class Vertex(val pos :Vector3, val uv :Vector2)

/**
 * face
 */
abstract class Face{
    def trianglate :Array[Triangle]
    type uv <: Seq[Vector2]
    var material=0
}

class Line(val i0 :Int, val i1 :Int) extends Face {
    def this()=this(0, 0)
    def this(indices :Seq[Int], uvs :Seq[Float])={
        this(indices(0), indices(1))
        uv(0)=Vector2(uvs(0), uvs(1))
        uv(1)=Vector2(uvs(2), uvs(3))
    }
    def this(i0 :Int, i1 :Int, src :Line)={
        this(i0, i1)
        uv(0)==src.uv(0)
        uv(1)==src.uv(1)
        material=src.material
    }
    val uv=new Array[Vector2](2)
        override def trianglate=Array()
}

class Triangle(val i0 :Int, val i1 :Int, val i2 :Int) 
extends Face {
    def this()=this(0, 0, 0)
    def this(indices :Seq[Int], uvs :Seq[Float])={
        this(indices(0), indices(1), indices(2))
        uv(0)=Vector2(uvs(0), uvs(1))
        uv(1)=Vector2(uvs(2), uvs(3))
        uv(2)=Vector2(uvs(4), uvs(5))
    }
    def this(i0 :Int, i1 :Int, i2 :Int, src :Triangle)={
        this(i0, i1, i2)
        uv(0)==src.uv(0)
        uv(1)==src.uv(1)
        uv(2)==src.uv(2)
        material=src.material
    }
    def this(i0 :Int, i1 :Int, i2 :Int, 
            uv0 :Vector2, uv1 :Vector2, uv2 :Vector2,
            m :Int)={
                this(i0, i1, i2)
                uv(0)=uv0
                uv(1)=uv1
                uv(2)=uv2
                material=m
    }
    val uv=new Array[Vector2](3)
        override def trianglate=Array(this)
}

class Quadrangle(val i0 :Int, val i1 :Int, val i2 :Int, val i3 :Int) 
extends Face {
    def this()=this(0, 0, 0, 0)
    def this(indices :Seq[Int], uvs :Seq[Float])={
        this(indices(0), indices(1), indices(2), indices(3))
        uv(0)=Vector2(uvs(0), uvs(1))
        uv(1)=Vector2(uvs(2), uvs(3))
        uv(2)=Vector2(uvs(4), uvs(5))
        uv(3)=Vector2(uvs(6), uvs(7))
    }
    def this(i0 :Int, i1 :Int, i2 :Int, i3 :Int, src :Quadrangle)={
        this(i0, i1, i2, i3)
        uv(0)==src.uv(0)
        uv(1)==src.uv(1)
        uv(2)==src.uv(2)
        uv(3)==src.uv(3)
        material=src.material
    }
    val uv=new Array[Vector2](4)
        override def trianglate=Array(
                new Triangle(i0, i1, i2, uv(0), uv(1), uv(2), material), 
                new Triangle(i2, i3, i0, uv(2), uv(3), uv(0), material))
}

