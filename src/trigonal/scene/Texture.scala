package trigonal.scene
import org.lwjgl.opengl.GL11._

class Texture(val w :Int, val h :Int, val channels :Int, 
        val data :java.nio.ByteBuffer) 
{
    val id=glGenTextures()
    glBindTexture(GL_TEXTURE_2D, id)
    channels match {
        case 3=>
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, 
                    GL_RGB, GL_UNSIGNED_BYTE, data)
        case 4=>
            glPixelStorei(GL_UNPACK_ALIGNMENT, 4)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, 
                    GL_RGBA, GL_UNSIGNED_BYTE, data)
    }
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

    def enable(){
        glEnable(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, id)
    }
    def disable(){
        glDisable(GL_TEXTURE_2D)
    }
}

