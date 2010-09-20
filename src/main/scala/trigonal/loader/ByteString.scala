package trigonal.loader
import java.nio.charset.Charset

class ByteString(val buf :Array[Byte]){
    override def toString() :String=new String(buf, Charset.forName("MS932")) 
    override def equals(other :Any) :Boolean={
        other match {
            case o :String => this==ByteString(o)
            case o :ByteString => buf.zip(o.buf).forall{
                p => p._1==p._2
            }
            case _ => false
        }
    }
}

object ByteString {
    private def createBuf(src :Array[Byte], start :Int, length :Int)
        :Array[Byte]={
        val buf=new Array[Byte](length)
        var pos=start
        for(i <-0 until length){
            val b=src(pos)
            b match {
                case 0 => return buf.slice(0, i)
                case _ =>
                    buf(i)=b
                    pos+=1
            }
        }
        buf        
    }
    def apply(src :Array[Byte], start :Int, length :Int) 
        :ByteString={
        new ByteString(createBuf(src, start, length))
    }
    def apply(src :String) :ByteString=apply(src.getBytes)
    def apply(src :Array[Byte]) :ByteString=apply(src, 0, src.length)
}

