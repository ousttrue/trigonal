package trigonal.loader
import trigonal.geometry._
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ByteReader(val path :File){
    val buf=createBuf(path)
    val bb=ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN)

    private def createBuf(path :File) :Array[Byte]={
        val buf=new Array[Byte](path.length.asInstanceOf[Int])
        val io=new FileInputStream(path)
        io.read(buf)
        io.close()
        buf
    }

    def pos :Int=bb.position
    def isEnd :Boolean=bb.position>=bb.capacity

    def getString(length :Int) :ByteString={
        val ret=ByteString(buf, bb.position, length)
        bb.position(bb.position()+length)
        ret
    }
    def get() :Int={
        val b=bb.get()
        if(b>=0) b else b+256
    }
    def getBytes(length :Int) :Array[Byte]={
        val ret=new Array[Byte](length)
        var position=bb.position
        for(i <-0 until length){
            ret(i)=buf(position)
            position+=1
        }
        bb.position(position)
        ret
    }
    def getWORD() :Int=get()+(get()<<8)
    def getDWORD() :Int=get()+(get()<<8) +(get()<<16) +(get()<<24)
    def getFloat() :Float=bb.getFloat()
    def getVector2() :Vector2=Vector2(getFloat(), getFloat())
    def getVector3() :Vector3={
        Vector3(getFloat(), getFloat(), getFloat())
    }
    def getQuaternion() :Quaternion=Quaternion(
        getFloat(), getFloat(), getFloat(), getFloat()
    )
    def getRGB() :RGB=RGB(getFloat(), getFloat(), getFloat())
    def getRGBA() :RGBA=RGBA(getFloat(), getFloat(), getFloat(), getFloat())
}

