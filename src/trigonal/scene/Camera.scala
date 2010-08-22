package trigonal.scene
import org.lwjgl.opengl.GL11
import trigonal.geometry.Vector3

class Camera(var distance :Float) extends Node {
    var head=0.0f
    var pitch=0.0f
    var shift_x=0.0f
    var shift_y=0.0f

    def apply(){
        GL11.glTranslatef(shift_x, shift_y, -distance);
        GL11.glRotatef(head, 0, 1, 0);
        GL11.glRotatef(pitch, 1, 0, 0);
    }

    def head(dx :Float){
        head+=dx
    }

    def pitch(dy :Float){
        pitch+=dy
    }

    def shift(dx :Int, dy :Int){
        shift_x+=dx
        shift_y+=dy
    }

    def dolly(d :Int){
    }
}

