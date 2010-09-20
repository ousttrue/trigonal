package trigonal.scene.immutable
import trigonal.scene.Node
import trigonal.scene.Material
import trigonal.geometry._
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils


class VertexArray ( 
        val positions :java.nio.FloatBuffer, 
        val uvArray :java.nio.FloatBuffer,
        val material :Option[Material]
        )
extends Node
{
    val vertexCount=positions.capacity/3

    override def drawSelf(){
        material match {
            case Some(m)=>m.begin()
            case None=>"no material"
        }
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        GL11.glVertexPointer(3, 0, positions)
        GL11.glTexCoordPointer(2, 0, uvArray)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount)
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
        material match {
            case Some(m)=>m.end()
            case None=>"no material"
        }
    }
}

object VertexArray {
    def apply(
            vertices :Seq[Vertex], 
            material :Option[Material]) 
        :VertexArray={
        val positions=BufferUtils.createFloatBuffer(vertices.length*3)
        val uvArray=BufferUtils.createFloatBuffer(vertices.length*2)
        for(v <- vertices){
            positions.put(v.pos.x)
            positions.put(v.pos.y)
            positions.put(v.pos.z)
            uvArray.put(v.uv.x)
            uvArray.put(v.uv.y)
        }
        positions.rewind()
        uvArray.rewind()
        new VertexArray(positions, uvArray, material)
    }
}

