package trigonal.geometry

class v(val x :Float, val y :Float, val z :Float)
object v{
    def apply(
            x :Int,
            y :Int,
            z :Int
            ) :v=new v(
                x.toString toFloat, 
                y.toString toFloat, 
                z.toString toFloat)
    def apply(
            x :{def toFloat:Float},
            y :{def toFloat:Float},
            z :{def toFloat:Float}
            ) :v=new v(x toFloat, y toFloat, z toFloat)
}
abstract class Face{
    def trianglate :Array[Triangle]
}
class Triangle(val i0 :Int, val i1 :Int, val i2 :Int) extends Face{
    override def trianglate=Array(this)
}
class Quadrangle(val i0 :Int, val i1 :Int, val i2 :Int, val i3 :Int) extends Face{
    override def trianglate=Array(
            new Triangle(i0, i1, i2), new Triangle(i2, i3, i0))
}

