import trigonal.lwjgl.Device
import trigonal.lwjgl.KeyboardEvent
import trigonal.lwjgl.MouseEvent
import trigonal.lwjgl.MouseDrag
import trigonal.lwjgl.MouseWheel
import trigonal.scene
import trigonal.loader.Loader
import org.lwjgl.input.Keyboard;
import java.io.File

object App {

    def main(args :Array[String]){

        // create window
        Device.create(800, 600, "test")
        Device.initializeOpenGL()

        // load scene
        val root=new scene.Empty()
        for(arg <- args){
            println("load: "+arg+"...")
            Loader.createNode(new File(arg)) match {
                case Some(node)=>
                    root.add(node)
                    println("loaded: "+arg)
                case None=>"fail to load"
            }
        }

        // create camera
        val camera=new scene.Camera(300)
        camera.resize(Device.width, Device.height)
        //camera.shift(0, -150)

        // set callback
        Device.addKeyboardCallback{
            case KeyboardEvent(Keyboard.KEY_ESCAPE, true)=>Device.close()
            case KeyboardEvent(Keyboard.KEY_Q, true)=>Device.close()
        }
        Device.addMouseCallback{
            // middle button
            case MouseDrag(2, dx, dy)=>
                camera.shift(-dx, -dy)
            // right button
            case MouseDrag(1, dx, dy)=>
                camera.head(-dx)
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

