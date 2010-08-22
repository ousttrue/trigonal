package trigonal.scene.immutable
import trigonal.scene.Node
import trigonal.geometry._
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils


class IndexedVertexArray (
        val positions :java.nio.FloatBuffer,
        val indices :java.nio.IntBuffer) 
extends Node
{
    override def updateSelf(){
    }

    override def drawSelf(){
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glVertexPointer(3, 0, positions)
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
    }
}

object IndexedVertexArray {
    def apply(vertices :Seq[Vector3] , triangles :Seq[Triangle]) :IndexedVertexArray={
        val positions=BufferUtils.createFloatBuffer(vertices.length*3)
        for(v <- vertices){
            positions.put(v.x)
            positions.put(v.y)
            positions.put(v.z)
        }
        positions.rewind()
        val indices=BufferUtils.createIntBuffer(triangles.length*3)
        for(t <- triangles){
            indices.put(t.i0)
            indices.put(t.i1)
            indices.put(t.i2)
        }
        indices.rewind()
        new IndexedVertexArray(positions, indices)
    }
}

class VertexArray ( val positions :java.nio.FloatBuffer )
    extends Node
{
    val vertexCount=positions.capacity/3

    override def updateSelf(){
    }

    override def drawSelf(){
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glVertexPointer(3, 0, positions)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
    }
}

object VertexArray {
    def apply(vertices :Seq[Vector3]) :VertexArray={
        val positions=BufferUtils.createFloatBuffer(vertices.length*3)
        for(v <- vertices){
            positions.put(v.x)
            positions.put(v.y)
            positions.put(v.z)
        }
        positions.rewind()
        new VertexArray(positions)
    }
}

