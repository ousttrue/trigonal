package trigonal.scene
import trigonal.geometry._
import trigonal.loader.TargaReader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.EXTAbgr
import org.lwjgl.BufferUtils
import java.io.File
import java.nio.ByteBuffer
import java.awt.image.DataBufferByte
import javax.imageio.ImageIO

class Material(val name :String, val color :RGBA)
{
    var texture :Option[Texture]=None
    var texturePath :Option[File]=None
    var isLoaded=false

    override def toString :String="<Material "+name+" "+color+")"

    // ToDo reverse tga bands.
    private def loadTargaImage(path :File)=TargaReader.read(path)
    private def loadImage(path :File)=ImageIO.read(path)

    private def createTexture(path :File) :Option[Texture]={
        println("create texture:", path)
        val image=if(path.getName().toLowerCase().endsWith(".tga")){
            loadTargaImage(path)
        }
        else{
            loadImage(path)
        }

        val data=image.getData().getDataBuffer().asInstanceOf[DataBufferByte]
        val bands=image.getSampleModel.getNumBands
        val buf=BufferUtils.createByteBuffer(
                bands*image.getWidth*image.getHeight)
        buf.put(data.getData)
        buf.rewind()
        bands match {
            case 3 =>
                Some(new Texture(
                        image.getWidth(), image.getHeight(), 3,
                        buf, GL12.GL_BGR))
            case 4 =>
                Some(new Texture(
                        image.getWidth(), image.getHeight(), 4,
                        buf, EXTAbgr.GL_ABGR_EXT))
            case _ =>
                None
        }
    }

    def load(){
        texture=texturePath match {
            case Some(path)=> createTexture(path)
            case None=> None
        }
        isLoaded=true
    }

    def begin(){
        if(!isLoaded){
            load()
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glFrontFace(GL11.GL_CW)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glColor4f(color.r, color.g, color.b, color.a)
        texture match {
            case Some(t)=> "tmp"
                t.enable()
            case None=> "No texture"
        }
    }

    def end(){
        texture match {
            case Some(t)=> "tmp"
                t.disable()
            case None=> "No texture"
        }
    }

}

