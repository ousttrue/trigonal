package trigonal.scene.immutable
import trigonal.scene.Node
import trigonal.scene.Material
import trigonal.geometry._
import org.lwjgl.opengl.GL11


class IndexArray(
        val indices :java.nio.IntBuffer,
        val material :Option[Material]
        )
{
    def draw(){
        material match {
            case Some(m)=>m.begin()
            case None=>"no material"
        }
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices)
        material match {
            case Some(m)=>m.end()
            case None=>"no material"
        }
    }
}


class IndexedVertexArray (
        val positions :java.nio.FloatBuffer,
        val uvArray :java.nio.FloatBuffer,
        val indexArrays: Seq[IndexArray]
        )
extends Node
{
    override def updateSelf(){
    }

    override def drawSelf(){
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        GL11.glVertexPointer(3, 0, positions)
        GL11.glTexCoordPointer(2, 0, uvArray)
        indexArrays.foreach{ _.draw() }
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
    }
}

