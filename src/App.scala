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

import scala.collection.mutable.ArrayBuffer
import trigonal.Device

package mutable {
    class VertexArray{
        val vertices=new ArrayBuffer[v]
        val triangles=new ArrayBuffer[Triangle]

        def addVertices(vertices :v*){
        }

        def addTriangles(triangles :Array[Triangle]){
        }

        def draw(){
        }
    }

    object VertexArray{
        def apply()=new VertexArray
    }
}

object App {

    def main(args :Array[String]){

        Device.create(800, 600, "test")
        
        val cube=mutable.VertexArray()
        cube.addVertices(
                v(-1, -1, -1),
                v( 1, -1, -1),
                v( 1,  1, -1),
                v( 1, -1, -1),
                v(-1, -1,  1),
                v( 1, -1,  1),
                v( 1,  1,  1),
                v( 1, -1,  1)
                )
        cube.addTriangles(
                Array(
                new Quadrangle(0, 1, 2, 3),
                new Quadrangle(5, 6, 2, 1),
                new Quadrangle(6, 7, 3, 2),
                new Quadrangle(7, 4, 0, 3),
                new Quadrangle(4, 5, 1, 0),
                new Quadrangle(5, 4, 7, 6)
                ).flatMap(_.trianglate))

        while(Device.isRunning){
            Device.update()

            cube.draw()

            Device.wait(60)
        }

        Device.destroy()
    }
}

