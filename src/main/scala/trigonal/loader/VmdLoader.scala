package trigonal.loader
import trigonal.scene
import trigonal.geometry._
import java.io.File
import scala.collection.mutable.ArrayBuffer

class VmdLoader extends Loader {
    class Motion(val name :ByteString, 
            val frame :Int, val pos :Vector3, val rot :Quaternion, 
            val interporate :Array[Byte])
    val motions=ArrayBuffer[Motion]()
    class Morph(val name :ByteString, val frame :Int, val weight :Float)
    val morphs=ArrayBuffer[Morph]()
    class Camera(val frame :Int, val distance :Float,
            val location :Vector3, val euler :Vector3, 
            val interpolation :Array[Byte], val viewAngle :Int, 
            val persepective :Int)
    val cameras=ArrayBuffer[Camera]()
    class Light(val frame :Int, val color :RGB, val location :Vector3)
    val lights=ArrayBuffer[Light]()
    class Shadow(val frame :Int, val mode :Int, val distance :Float)
    val shadows=ArrayBuffer[Shadow]()

    override def accept(path :File)=path.toString.toLowerCase.endsWith(".vmd")

    override def load(path :File) :Boolean={
        val io=new ByteReader(path)

        println(io.getString(30))
        println(io.getString(20))

        val motionCount=io.getDWORD()
        for(i <-0 until motionCount){
            motions.append(new Motion(io.getString(15), io.getDWORD(),
                        io.getVector3(), io.getQuaternion(), io.getBytes(64)
                        ))
        }

        val morphCount=io.getDWORD()
        for(i <-0 until morphCount){
            morphs.append(new Morph(io.getString(15),
                        io.getDWORD(), io.getFloat()
                        ))
        }

        if(io.isEnd){
            return true
        }
        val cameraCount=io.getDWORD()
        for(i <-0 until cameraCount){
            cameras.append(new Camera(io.getDWORD(),
                        io.getFloat(), io.getVector3(), io.getVector3(),
                        io.getBytes(24), io.getDWORD(), io.get()
                        ))
        }

        if(io.isEnd){
            return true
        }
        val lightCount=io.getDWORD()
        for(i <-0 until lightCount){
            lights.append(new Light(io.getDWORD(), 
                        io.getRGB(), io.getVector3()
                        ))
        }

        if(io.isEnd){
            return true
        }
        val shadowCount=io.getDWORD()
        for(i <-0 until shadowCount){
            shadows.append(new Shadow(io.getDWORD(), io.get(), io.getFloat()))
        }

        assert(io.isEnd)
        return true
    }

    override def build(dir :File) :Any={
        val sceneMotion=new scene.Motion(30)
        for(m <- motions){
            sceneMotion.addKeyFrame(
                    m.name.toString, m.frame, new scene.Key(m.pos, m.rot))
        }
        sceneMotion
    }
}

