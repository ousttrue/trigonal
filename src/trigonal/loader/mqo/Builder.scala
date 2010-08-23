package trigonal.loader.mqo

import trigonal.scene
import trigonal.geometry._
import scala.collection.mutable.ArrayBuffer

object Builder {
    def createVertexArray(loader :trigonal.loader.mqo.Loader) :scene.Node={
        // create materials 
        val materials=loader.materials.map{
            m => Some(new scene.Material(m.name, m.color)) :Option[scene.Material]
        }
        // create vertex buffers and uv buffers
        val verticesEachMaterial=loader.materials.map{
            _ => ArrayBuffer[Vertex]()
        }
        if(verticesEachMaterial.isEmpty){
            // fail safe
            materials.append(None)
            verticesEachMaterial.append(ArrayBuffer[Vertex]())
        }
        // distribute vertices to materials
        for(o <- loader.objects; f <- o.faces; t <- f.trianglate){
            val vertices=verticesEachMaterial(f.material)
            vertices.append(new Vertex(o.vertices(t.i0), t.uv(0)))
            vertices.append(new Vertex(o.vertices(t.i1), t.uv(1)))
            vertices.append(new Vertex(o.vertices(t.i2), t.uv(2)))
        }
        // create VertexArray for each material
        val top=new scene.Empty()
        for((vertices, material) <- verticesEachMaterial.zip(materials);
                if vertices.length>0){
            top.add(scene.immutable.VertexArray(vertices, material))
        }

        top
    }
}

