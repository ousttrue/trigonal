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

class Vector3(val data :Array[Float]){
    override def toString="[%f %f %f]".format(data(0), data(1), data(2))
    def x=data(0)
    def y=data(1)
    def z=data(2)
    def apply(matrix :Matrix3)={
        Vector3(
            this.innerProduct(matrix.col1),
            this.innerProduct(matrix.col2),
            this.innerProduct(matrix.col3)
        )
    }
    def +(other :Vector3) :Vector3=new Vector3(data.zip(other.data).map{
        p => p._1+p._2
    })
    def -(other :Vector3) :Vector3=new Vector3(data.zip(other.data).map{
        p => p._1-p._2
    })
    def unary_- :Vector3=new Vector3(data.map{-_})
    def *(factor :Float)=new Vector3(data.map{_*factor})
    def innerProduct(other :Vector3)=data.zip(other.data).map{
        p => p._1*p._2
    }.foldLeft(0.0f){
        _+_
    }
}
object Vector3 {
    def apply(x :Float, y :Float, z :Float) :Vector3=new Vector3(Array(x, y, z))
    def apply(
            x :Int,
            y :Int,
            z :Int
            ) =new Vector3(Array(
                    x.toString toFloat,
                    y.toString toFloat,
                    z.toString toFloat))
    def apply(
            x :{def toFloat:Float},
            y :{def toFloat:Float},
            z :{def toFloat:Float}
            ) :Vector3=new Vector3(Array(
                    x toFloat,
                    y toFloat,
                    z toFloat))
    def zero()=new Vector3(Array(0, 0, 0))
}

class Quaternion(val x :Float, val y :Float, val z :Float, val w :Float){
    override def toString="[%f %f %f; %f]".format(x, y, z, w)
    def toMatrix() :Matrix3={
      val xx=x*x;
      val yy=y*y;
      val zz=z*z;

      val xy=x*y;
      val yz=y*z;
      val zx=z*x;

      val wx=w*x;
      val wy=w*y;
      val wz=w*z;

      new Matrix3(Array(
          1-2*(yy+zz), 2*(xy+wz), 2*(zx-wy),
          2*(xy-wz), 1-2*(xx+zz), 2*(yz+wx),
          2*(zx+wy), 2*(yz-wx), 1-2*(xx+yy)
          ));
    }
}
object Quaternion {
    def apply(x :Float, y :Float, z :Float, w :Float) :Quaternion=
        new Quaternion(x, y, z, w)
}

class Transform(val rot :Matrix3, val pos :Vector3) {
    def apply(v :Vector3)=v.apply(rot)+pos
    def * (other :Transform)=new Transform(
            rot * other.rot, pos.apply(other.rot)+other.pos)
    def + (other :Vector3)=new Transform(
            rot, pos+other)
}
object Transform {
    def apply() :Transform=new Transform(Matrix3.identity, Vector3.zero)
    def apply(rot: Quaternion, pos: Vector3)=
    new Transform(rot.toMatrix, pos)
    def apply(pos: Vector3)=new Transform(Matrix3.identity, pos)
}

class Matrix3(val data :Array[Float]) {
    def -(rhs :Matrix3)=new Matrix3(data.zip(rhs.data).map{
            p => p._1-p._2
            })
    def *(rhs: Matrix3)=Matrix3(
        row1 innerProduct rhs.col1,
        row1 innerProduct rhs.col2,
        row1 innerProduct rhs.col3,
        row2 innerProduct rhs.col1,
        row2 innerProduct rhs.col2,
        row2 innerProduct rhs.col3,
        row3 innerProduct rhs.col1,
        row3 innerProduct rhs.col2,
        row3 innerProduct rhs.col3
    )

    def col1()=Vector3(data(0), data(1), data(2))
    def col2()=Vector3(data(3), data(4), data(5))
    def col3()=Vector3(data(6), data(7), data(8))

    def row1()=Vector3(data(0), data(3), data(6))
    def row2()=Vector3(data(1), data(4), data(7))
    def row3()=Vector3(data(2), data(5), data(8))
}
object Matrix3{
    def apply(
            e_11 :Float, e_12 :Float, e_13 :Float,
            e_21 :Float, e_22 :Float, e_23 :Float,
            e_31 :Float, e_32 :Float, e_33 :Float)=new Matrix3(Array(
                    e_11, e_12, e_13,
                    e_21, e_22, e_23,
                    e_31, e_32, e_33
                    ))
    def identity()=new Matrix3(Array(
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
            ))
}


/**
 * color
 */
class RGB(val r :Float, val g :Float, val b :Float){
    override def toString="<RGB %f:%f:%f>".format(r, g, b)
}
object RGB {
    def apply(r :Float, g :Float, b :Float) :RGB=new RGB(r, g, b)
    def apply(
            r :{def toFloat:Float},
            g :{def toFloat:Float},
            b :{def toFloat:Float}
            ) :RGB=new RGB(r toFloat, g toFloat, b toFloat)
}

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

