package trigonal.lwjgl
import scala.collection.mutable.ArrayBuffer

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

case class KeyboardEvent(val keyCode :Int, val isDown :Boolean)
abstract class MouseEvent
case class MouseDrag(val button :Int, val dx :Int, val dy :Int) extends MouseEvent
case class MouseWheel(val d :Int) extends MouseEvent

object Device{

    var isClose=false
    val keyboardCallbacks=ArrayBuffer[PartialFunction[KeyboardEvent, Unit]]()
    val mouseCallbacks=ArrayBuffer[PartialFunction[MouseEvent, Unit]]()

    // mouse status
    var mouse0=false
    var mouse1=false
    var mouse2=false
    var mouse_x=0
    var mouse_y=0

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

    def initializeOpenGL() {
        GL11.glViewport(0, 0, Device.width, Device.height);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        //GL11.glOrtho(0.0, Device.width, 0.0, Device.height, -100.0, 100.0);
        GLU.gluPerspective(30, 1, 1, 1000)
    }

    def clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    def addKeyboardCallback(f :PartialFunction[KeyboardEvent, Unit]){
        keyboardCallbacks.append(f)
    }

    def addMouseCallback(f :PartialFunction[MouseEvent, Unit]){
        mouseCallbacks.append(f)
    }

    private def dispatchMouse(event :MouseEvent){
        for(f <- mouseCallbacks; if f.isDefinedAt(event)){
            f.apply(event)
        }
    }

    def dispatch(){
        // keyboard
        while(Keyboard.next()){
            val event=KeyboardEvent(
                    Keyboard.getEventKey, Keyboard.getEventKeyState)
            for(f <- keyboardCallbacks; if f.isDefinedAt(event)){
                f.apply(event)
            }
        }
        // mouse
        while(Mouse.next()){
           Mouse.getEventButton match {
               case 0=> 
                   mouse0=Mouse.getEventButtonState
               case 1=> 
                    mouse1=Mouse.getEventButtonState
               case 2=> 
                   mouse2=Mouse.getEventButtonState
               case _=> 
                   val x=Mouse.getEventX()
                   val y=Mouse.getEventY()
                   if(mouse0){
                       dispatchMouse(MouseDrag(0, mouse_x-x, mouse_y-y))
                   }
                   if(mouse1){
                       dispatchMouse(MouseDrag(1, mouse_x-x, mouse_y-y))
                   }
                   if(mouse2){
                       dispatchMouse(MouseDrag(2, mouse_x-x, mouse_y-y))
                   }
                   val d=Mouse.getDWheel()
                   if(d!=0){
                       dispatchMouse(MouseWheel(d))
                   }
                   mouse_x=x
                   mouse_y=y
           }
        }
    }
}

