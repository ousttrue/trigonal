package trigonal.loader
import java.io.File
import trigonal.scene.Node

abstract class Loadable {
    def accept(path :File) :Boolean
    def load(path :File) :Option[Buildable]
}

abstract class Buildable

object Loader {
    def apply(path :File) :Option[Buildable]={
        for(l <- Array(MqoLoader, PmdLoader); if(l.accept(path))){
            l.load(path) match {
                case Some(buildable)=> return Some(buildable)
                case None=>"fail to load"
            }
        }
        None
    }
}

object Builder {
    def apply(src :Buildable, path :File) :Node={
        src match {
            case l :MqoLoader => MqoBuilder.createVertexArray(l, path)
            case l :PmdLoader => PmdBuilder.createVertexArray(l, path)
        }
    }
}

