package trigonal.scene
import trigonal.geometry._
import org.lwjgl.opengl.GL11

class Material(val name :String, val color :RGBA)
{
    /*
    var texture :Option[Texture]=None
    var texturePath :Option[File]=None
    */
    var isLoaded=false

    override def toString :String="<Material "+name+" "+color+")"

    /*
    private def createTargaTexture(path :File) :Option[Texture]={
        val r=new TargaReader 
        val image=r.read(path)
        val buf=image.getData().getDataBuffer().asInstanceOf[DataBufferByte]
        r.bands match {
            case 3 =>
                val data=
                new TextureData(GL11.GL_RGB, 
                        image.getWidth(), image.getHeight(), 0, 
                        GL11.GL_BGR, GL11.GL_UNSIGNED_BYTE, 
                        false, false, false, 
                        ByteBuffer.wrap(buf.getData()), null)
                Some(new Texture(data))
            case 4 =>
                val data=
                new TextureData(GL11.GL_RGBA, 
                        image.getWidth(), image.getHeight(), 0, 
                        GL11.GL_ABGR_EXT, GL11.GL_UNSIGNED_BYTE, 
                        false, false, false, 
                        ByteBuffer.wrap(buf.getData()), null)
                Some(new Texture(data))
            case _ => None
        }
    }
    */

    def load(){
        /*
        texturePath match {
            case Some(path)=>
                println("create texture:", path)
                if(path.getName().toLowerCase().endsWith(".tga")){
                    texture=createTargaTexture(path)
                }
                else{
                    texture=Some(TextureIO.newTexture(path, true))
                }
            case None=> "No texture"
        }
        */
        isLoaded=true
    }

    def begin(){
        if(!isLoaded){
            load()
        }
        GL11.glColor4f(color.r, color.g, color.b, color.a)
        /*
        texture match {
            case Some(t)=> "tmp"
                t.enable()
                t.bind()
            case None=> "No texture"
        }
        */
    }

    def end(){
        /*
        texture match {
            case Some(t)=> "tmp"
                t.disable()
            case None=> "No texture"
        }
        */
    }

}

