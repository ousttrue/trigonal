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
        var skeleton :Option[scene.Skeleton]=None
        for(arg <- args){
            println("load: "+arg+"...")
            Loader.load(new File(arg)) match {
                case node :scene.immutable.SkeletalIndexedVertexArray=>
                    root.add(node)
                    println(node)
                    skeleton=Some(node.skeleton)

                case node :scene.Node=>
                    root.add(node)
                    println(node)

                case motion :scene.Motion=>
                    println(motion)
                    skeleton match {
                        case Some(s) => 
                            println("set motion")
                            s.setMotion(motion)
                        case _=>"no model"
                    }

                case some =>
                    println("fail to load: "+arg)
            }
        }

        // create camera
        val camera=new scene.Camera(100)
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
            // event dispatch
            val time=Device.dispatch()
            // update frame
            root.update(time)
            // clear
            Device.clear()
            // draw
            camera.apply()
            root.draw()
            // sync
            Device.sync(30)
        }

        // clean up
        Device.destroy()
    }
}

