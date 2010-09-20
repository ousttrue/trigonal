package trigonal.scene.immutable
import trigonal.scene.Node
import trigonal.scene.Material
import trigonal.scene.Motion
import trigonal.scene.Skeleton
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


class SkeletalIndexedVertexArray(
        positions :java.nio.FloatBuffer,
        uvArray :java.nio.FloatBuffer,
        indexArrays: Seq[IndexArray],
        val boneWeights :Seq[(Vector3, Int, Int, Float)],
        val skeleton :Skeleton
        )
extends IndexedVertexArray(positions, uvArray, indexArrays)
{
    override def updateSelf(time :Int){
        positions.rewind()
        skeleton.update(time)
        for((v, b0, b1, weight0) <- boneWeights){
            val newPosition=skeleton.applyTransform(v, b0, b1, weight0)
            positions.put(newPosition.x)
            positions.put(newPosition.y)
            positions.put(newPosition.z)
        }
        positions.rewind()
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

