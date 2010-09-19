package trigonal.scene
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU;
import trigonal.geometry.Vector3

class Camera(var distance :Float) extends Node {
    var head=0.0f
    var pitch=0.0f
    var shift_x=0.0f
    var shift_y=0.0f

    var fovy=30.0f;
    var aspect=1.0f;
    var near=1.0f;
    var far=50000.0f;

    def apply(){
        GL11.glTranslatef(shift_x, shift_y, -distance);
        GL11.glRotatef(head, 0, 1, 0);
        GL11.glRotatef(pitch, 1, 0, 0);
    }

    def resize(w :Int, h :Int){
        GL11.glViewport(0, 0, w, h);

        if(h!=0.0f){
            aspect=w.toFloat/h.toFloat
        }

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(fovy, aspect, near, far)
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
        if(d>0){
            distance*=0.9f
        }
        else if(d<0){
            distance*=1.1f
        }
    }
}

