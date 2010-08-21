package trigonal.lwjgl

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

object Device{

    def isRunning= !Display.isCloseRequested

    def isActive=Display.isActive

    def update(){
        Display.update();
    }

    def wait(fps :Int){
        Display.sync(fps);
    }

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
}

