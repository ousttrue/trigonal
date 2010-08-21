import trigonal.lwjgl.Device
import trigonal.scene.mutable.VertexArray
import trigonal.geometry._

object App {

    def main(args :Array[String]){

        Device.create(800, 600, "test")
        
        val cube=VertexArray()
        cube.addVertices(Array(
                v(-1, -1, -1),
                v( 1, -1, -1),
                v( 1,  1, -1),
                v( 1, -1, -1),
                v(-1, -1,  1),
                v( 1, -1,  1),
                v( 1,  1,  1),
                v( 1, -1,  1)
                ))
        cube.addTriangles(
                Array(
                new Quadrangle(0, 1, 2, 3),
                new Quadrangle(5, 6, 2, 1),
                new Quadrangle(6, 7, 3, 2),
                new Quadrangle(7, 4, 0, 3),
                new Quadrangle(4, 5, 1, 0),
                new Quadrangle(5, 4, 7, 6)
                ).flatMap(_.trianglate))

        while(Device.isRunning){
            Device.update()

            cube.draw()

            Device.wait(60)
        }

        Device.destroy()
    }
}

