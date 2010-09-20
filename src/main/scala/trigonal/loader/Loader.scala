package trigonal.loader
import java.io.File
import trigonal.scene.Node

abstract class Loader {
    def accept(path :File) :Boolean
    def load(path :File) :Boolean
    def build(dir :File) :Any
}

object Loader {
    val loaders=Array(new MqoLoader, new PmdLoader, new VmdLoader)

    def load(path :File) :Any={
        val dir=path.getParentFile()
        for(l <- loaders; if(l.accept(path))){
            if(l.load(path)){
                return l.build(dir)
            }
        }
    }
}

