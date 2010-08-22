import trigonal.lwjgl.Device
import trigonal.scene

import trigonal.geometry._
import scala.collection.mutable.ArrayBuffer

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard;

object App {

    def createVertexArray(loader :trigonal.loader.mqo.Loader) :scene.Node={

        val verticesEachMaterial=loader.materials.map(
                _ => ArrayBuffer[Vector3]())
        if(verticesEachMaterial.isEmpty){
            // fail safe
            verticesEachMaterial.append(ArrayBuffer[Vector3]())
        }

        for(o <- loader.objects; f <- o.faces; t <- f.trianglate){
            verticesEachMaterial(f.material).append(o.vertices(t.i0))
            verticesEachMaterial(f.material).append(o.vertices(t.i1))
            verticesEachMaterial(f.material).append(o.vertices(t.i2))
        }

        val top=new scene.Empty()
        for((vertices, material_index) <- verticesEachMaterial.zipWithIndex;
                if vertices.length>0){
            top.add(scene.immutable.VertexArray(vertices))
        }

        top
    }

    def main(args :Array[String]){

        // create window
        Device.create(800, 600, "test")
        Device.initializeOpenGL()

        // load scene
        val root=new scene.Empty()
        for(arg <- args){
            print("load: "+arg+"...")
            val loader=new trigonal.loader.mqo.Loader()
            if(loader.load(arg)){
                root.add(createVertexArray(loader))
                println("success")
            }
            else{
                println("failed")
            }
        }

        // create camera
        val camera=new scene.Camera(Vector3(0, 0, -800))
        root.add(camera)

        // set callback
        Device.addKeyDownCallback(Keyboard.KEY_ESCAPE){
            ()=>Device.close()
        }
        Device.addKeyDownCallback(Keyboard.KEY_Q){
            ()=>Device.close()
        }

        // main loop
        while(Device.isRunning){
            // update frame
            Device.keyDownDispatch()
            root.update()

            // draw
            Device.clear()

            camera.apply()
            root.draw()

            // update
            Device.update()
            Device.sync(60)
        }

        // clean up
        Device.destroy()
    }
}

