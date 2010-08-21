import trigonal.lwjgl.Device
import trigonal.scene.mutable
import trigonal.scene.immutable
import trigonal.geometry._

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard;

object App {

    def main(args :Array[String]){

        // create window
        Device.create(800, 600, "test")
        Device.initializeOpenGL()
        
        // create scene
        val cube=immutable.VertexArray(Array(
                    v(-1, -1, -1),
                    v( 1, -1, -1),
                    v( 1,  1, -1),
                    v(-1,  1, -1),
                    v(-1, -1,  1),
                    v( 1, -1,  1),
                    v( 1,  1,  1),
                    v(-1,  1,  1)
                ), 
                Array(
                    new Quadrangle(0, 1, 2, 3),
                    new Quadrangle(5, 6, 2, 1),
                    new Quadrangle(6, 7, 3, 2),
                    new Quadrangle(7, 4, 0, 3),
                    new Quadrangle(4, 5, 1, 0),
                    new Quadrangle(5, 4, 7, 6)
                    ).flatMap(_.trianglate)
                )

        // set callback
        Device.addKeyDownCallback(Keyboard.KEY_ESCAPE){
            ()=>Device.close()
        }
        Device.addKeyDownCallback(Keyboard.KEY_Q){
            ()=>Device.close()
        }

        while(Device.isRunning){
            // update frame
            Device.keyDownDispatch()
            cube.update()

            // draw
            Device.clear()

            GL11.glTranslatef(0.0f, 0.0f, -10.0f)
            cube.draw()

            // update
            Device.update()
            Device.sync(60)
        }

        Device.destroy()
    }
}

