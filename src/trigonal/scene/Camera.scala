package trigonal.scene
import org.lwjgl.opengl.GL11
import trigonal.geometry.Vector3

class Camera(val pos :Vector3) extends Node {
    def this()=this(Vector3(0, 0, 0))
    def apply(){
        GL11.glTranslatef(pos.x, pos.y, pos.z);
    }
}

