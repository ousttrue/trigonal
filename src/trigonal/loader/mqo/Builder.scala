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
        // create vertex buffers
        val verticesEachMaterial=loader.materials.map{
            _ => ArrayBuffer[Vector3]()
        }
        if(verticesEachMaterial.isEmpty){
            // fail safe
            materials.append(None)
            verticesEachMaterial.append(ArrayBuffer[Vector3]())
        }
        // distribute vertices to materials
        for(o <- loader.objects; f <- o.faces; t <- f.trianglate){
            verticesEachMaterial(f.material).append(o.vertices(t.i0))
            verticesEachMaterial(f.material).append(o.vertices(t.i1))
            verticesEachMaterial(f.material).append(o.vertices(t.i2))
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

