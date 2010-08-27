package trigonal.loader
import java.io.File
import trigonal.scene.Node

abstract class Loader {
    def accept(path :File) :Boolean
    def load(path :File) :Boolean
    def build(dir :File) :Option[Node]
}

object Loader {
    val loaders=Array(new MqoLoader, new PmdLoader)

    def createNode(path :File) :Option[Node]={
        val dir=path.getParentFile()
        for(l <- loaders; if(l.accept(path))){
            if(l.load(path)){
                l.build(dir) match {
                    case Some(node)=> return Some(node)                    
                    case None=> "fail to build"
                }
            }
        }
        None
    }
}

