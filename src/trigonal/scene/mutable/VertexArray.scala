package trigonal.scene.mutable
import trigonal.geometry._

import scala.collection.mutable.ArrayBuffer

class VertexArray{
    val vertices=new ArrayBuffer[v]
    val triangles=new ArrayBuffer[Triangle]

    var angle=0.0f;

    def addVertices(src :Array[v]){
        for(vertex <- src){ vertices.append(vertex) }
    }

    def addTriangles(src :Array[Triangle]){
        for(triangle <- src){ triangles.append(triangle) }
    }
}

object VertexArray{
    def apply()=new VertexArray
}

