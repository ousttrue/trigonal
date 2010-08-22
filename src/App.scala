import trigonal.lwjgl.Device
import trigonal.scene
import trigonal.geometry._

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard;

object App {

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
                root.add(trigonal.loader.mqo.Builder.createVertexArray(loader))
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

