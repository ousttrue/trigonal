package trigonal.scene

trait Node {
    var children=scala.collection.mutable.ArrayBuffer[Node]()
    def add(child :Node){ children.append(child) }

    def updateSelf(time :Int){}
    def update(time :Int){ 
        updateSelf(time)
        children.foreach{ _.update(time) } 
    }

    def drawSelf(){}
    def draw(){ 
        drawSelf()
        children.foreach{ _.draw() } 
    }
}

