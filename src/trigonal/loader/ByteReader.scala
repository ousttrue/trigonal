package trigonal.loader
import trigonal.geometry._
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.ByteBuffer


class ByteReader(val path :File){
    val CP932=Charset.forName("MS932")

    val buf=new Array[Byte](path.length.asInstanceOf[Int])
    val io=new FileInputStream(path)
    io.read(buf)
    io.close()
    val bb=ByteBuffer.wrap(buf)

    def pos :Int=bb.position
    def isEnd :Boolean=bb.position>=bb.capacity

    def getString(length :Int) :String={
        val ret=new String(buf, bb.position, length, CP932)
        bb.position(bb.position()+length)
        ret
    }
    def get() :Int={
        val b=bb.get()
        if(b>=0) b else b+256
    }
    def getWORD() :Int={
        val b0=get()
        val b1=get()
        (b1<<8) + b0
    }
    def getDWORD() :Int={
        val b0=get()
        val b1=get()
        val b2=get()
        val b3=get()
        (b3<<24) + (b2<<16) + (b1<<8) + b0
    }
    def getFloat() :Float=bb.getFloat()
    def getVector2() :Vector2=Vector2(getFloat(), getFloat())
    def getVector3() :Vector3=Vector3(getFloat(), getFloat(), getFloat())
    def getRGB() :RGB=RGB(getFloat(), getFloat(), getFloat())
    def getRGBA() :RGBA=RGBA(getFloat(), getFloat(), getFloat(), getFloat())
}

