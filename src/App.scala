import trigonal.lwjgl.Device
import trigonal.lwjgl.KeyboardEvent
import trigonal.lwjgl.MouseEvent
import trigonal.lwjgl.MouseDrag
import trigonal.lwjgl.MouseWheel
import trigonal.scene
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
        val camera=new scene.Camera(800)
        root.add(camera)

        // set callback
        Device.addKeyboardCallback{
            case KeyboardEvent(Keyboard.KEY_ESCAPE, true)=>Device.close()
            case KeyboardEvent(Keyboard.KEY_Q, true)=>Device.close()
        }
        Device.addMouseCallback{
            // middle button
            case MouseDrag(2, dx, dy)=>
                camera.shift(dx, dy)
            // right button
            case MouseDrag(1, dx, dy)=>
                camera.head(dx)
                camera.pitch(dy)
            case MouseWheel(d)=>
                camera.dolly(d)
        }

        // main loop
        while(Device.isRunning){
            // update frame
            Device.dispatch()
            root.update()
            // clear
            Device.clear()
            // draw
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

