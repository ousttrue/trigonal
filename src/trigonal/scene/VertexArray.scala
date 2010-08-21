package trigonal.scene
import trigonal.geometry._

package mutable {
    import scala.collection.mutable.ArrayBuffer

    class VertexArray{
        val vertices=new ArrayBuffer[v]
        val triangles=new ArrayBuffer[Triangle]

        def addVertices(vertices :Array[v]){
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

