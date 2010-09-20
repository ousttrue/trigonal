package trigonal.scene
import trigonal.geometry._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

class Key(val pos: Vector3, val rot: Quaternion){
    override def toString="<%s %s>".format(pos, rot)
}

class Channel[T](val name :String){
    val keyFrames=ArrayBuffer[(Int, T)]()
    def addKeyFrame(frame :Int, key :T){
        keyFrames.append(frame -> key)
    }
    def sort{ keyFrames.sortWith(_._1 < _._1) }
    def get(target :Int) :T={
        // first
        if(target<=keyFrames.head._1){
            return keyFrames.head._2
        }
        // linear search
        for(sliding <- keyFrames.sliding(2)){
            if(target==sliding.head._1){
                return sliding.head._2
            }
            else if(target<sliding.last._1){
                // interpolate
                return sliding.head._2
            }
        }
        // last
        return keyFrames.last._2
    }
}

class Motion(val fps :Int) {
    val channels=ArrayBuffer[Channel[Key]]()
    val channelMap=Map[String, Channel[Key]]()

    def timeToFrame(time :Int) :Int=(time/fps).round
    def addKeyFrame(name :String, frame :Int, key :Key){
        getChannel(name).addKeyFrame(frame, key)
    }
    private def getChannel(name :String)={
        if(channelMap.contains(name)){
            channelMap(name)
        }
        else{
            createChannel(name)
        }
    }
    private def createChannel(name :String)={
        val channel=new Channel[Key](name)
        channels.append(channel)
        channelMap.put(name, channel)
        channel
    }
}

