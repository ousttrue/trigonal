package trigonal.lwjgl

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

object Device{

    var isClose=false
    val keyDownMap=scala.collection.mutable.Map[Int, ()=>Unit]()

    def isRunning= !isClose && !Display.isCloseRequested
    def isActive=Display.isActive
    def update(){ Display.update() }
    def sync(fps :Int){ Display.sync(fps) }
    def width=Display.getDisplayMode.getWidth
    def height=Display.getDisplayMode.getHeight
    def close(){ isClose=true }

    def create(w :Int, h :Int, title :String){
        val dm=org.lwjgl.util.Display.getAvailableDisplayModes(w, h, -1, -1, -1, -1, 60, 60)
        org.lwjgl.util.Display.setDisplayMode(dm, Array[String](
                    "width=" + w,
                    "height=" + h,
                    "freq=" + 60,
                    "bpp=" + Display.getDisplayMode().getBitsPerPixel()
                    ))
        Display.setFullscreen(false);
        Display.create();
        Display.setTitle(title);
        Keyboard.create();
        Mouse.create();
        Mouse.setGrabbed(false);
    }

    def destroy(){
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }

    def initialize() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, Device.width, 0.0, Device.height, -1.0, 1.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, Device.width, Device.height);
    }

    def clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    def addKeyDownCallback(keyCode :Int)(f : ()=>Unit){
        keyDownMap+=(keyCode -> f)
    }

    def keyDownDispatch(){
        for((keyCode, f) <- keyDownMap){
            if(Keyboard.isKeyDown(keyCode)){
                f()
            }
        }
    }
}

