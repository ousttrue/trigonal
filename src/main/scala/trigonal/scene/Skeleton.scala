package trigonal.scene
import trigonal.geometry._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import org.lwjgl.opengl.GL11

class BoneBuilder(val name :String, val pos :Vector3) {
    val children=ArrayBuffer[BoneBuilder]()
    var localPos=pos
    def add(child :BoneBuilder)={
        children+=(child)
        child.localPos=child.pos-pos
        child
    }
    def result() :Bone={
        new Bone( name, localPos, -pos, children.map{_.result}.toArray)
    }
}

class Bone(val name :String, 
val localPos :Vector3, val inversedPos :Vector3,
val children :Array[Bone]) 
extends Traversable[Bone]{
    var channel :Option[Channel[Key]]=None
    var transform=Transform(localPos)
    var accumulate=Transform()
    override def toString()="<Bone %s %s>".format(name)
    override def foreach[U](f: (trigonal.scene.Bone) => U){
        for(child <- children){
            f(child)
            child.foreach(f)
        }
    }
    def recursive[U](
            f: (trigonal.scene.Bone, trigonal.scene.Bone) => U){
        for(child <- children){
            f(this, child)
            child.recursive(f)
        }
    }
    def calcAccumulate(parent :Transform){ 
        accumulate=parent * transform + localPos 
    }
    def setFrame(frame :Int){
        channel match {
            case Some(ch)=>
                val key=ch.get(frame)
                transform=Transform(key.rot, key.pos)
            case None=>"no channel"
        }
        accumulate=Transform()
    }
    def getCalced=Transform(-inversedPos) * accumulate
    def setChannel(someChannel :Option[Channel[Key]]){ channel=someChannel }

    def draw(){
        GL11.glPushMatrix()
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glVertex3f(transform.pos.x, transform.pos.y, transform.pos.z)
        GL11.glEnd()
        GL11.glMultMatrix(transform.matrix)
        GL11.glPointSize(2);
        GL11.glBegin(GL11.GL_POINTS)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glEnd()

        children.foreach(_.draw())

        GL11.glPopMatrix()
    }
}

class Skeleton(val root :Bone)
extends Node
{
    val bones=ArrayBuffer[Bone]()
    val boneMap=Map[String, Bone]()
    var motion :Option[Motion]=None
    for(b :Bone <- root){
        bones.append(b)
        boneMap.put(b.name, b)
    }

    def setMotion(m :Motion){
        motion=Some(m)
        for(channel <- m.channels){
            if(boneMap.contains(channel.name)){
                boneMap(channel.name).setChannel(Some(channel))
            }
        }
    }

    override def drawSelf(){ root.draw() }

    override def update(time :Int){
        motion match {
            case Some(m)=>
                val frame=m.timeToFrame(time)
                root.recursive{
                    (p, b) =>
                        b.setFrame(frame)
                        b.calcAccumulate(p.accumulate)
                }
            case None=>"no motion"
        }
    }

    def applyTransform(
            pos :Vector3, b0 :Int, b1 :Int, weight0 :Float) :Vector3={
        val EPSILON=0.001f
        if(weight0>1-EPSILON){
            getTransform(b0).apply(pos)
        }
        else if(weight0<EPSILON){
            getTransform(b1).apply(pos)
        }
        else{
            val t0=getTransform(b0)
            val t1=getTransform(b1)
            t0.apply(pos)*weight0+t1.apply(pos)*(1.0f-weight0)
        }
    }

    private def getTransform(index :Int)=bones(index).getCalced
}

