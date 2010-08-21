package trigonal.scene.immutable
import trigonal.geometry._
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils

class VertexArray(
        val vertexArray :java.nio.FloatBuffer,
        val indices :java.nio.IntBuffer)
{
    def update(){
    }

    def draw(){
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glVertexPointer(3, 0, vertexArray)
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
    }
}

object VertexArray {
    def apply(vertices :Seq[v] , triangles :Seq[Triangle]) :VertexArray={
        val vertexArray=BufferUtils.createFloatBuffer(vertices.length*3)
        val indices=BufferUtils.createIntBuffer(triangles.length*3)
        for(v <- vertices){
            vertexArray.put(v.x)
            vertexArray.put(v.y)
            vertexArray.put(v.z)
        }
        vertexArray.rewind()
        for(t <- triangles){
            indices.put(t.i0)
            indices.put(t.i1)
            indices.put(t.i2)
        }
        indices.rewind()
        new VertexArray(vertexArray, indices)
    }
}

