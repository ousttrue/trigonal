package trigonal.scene

trait Node {
    var children=scala.collection.mutable.ArrayBuffer[Node]()
    def updateSelf(){}
    def update(){ 
        updateSelf()
        children.foreach{ _.update() } 
    }
    def drawSelf(){}
    def draw(){ 
        drawSelf()
        children.foreach{ _.draw() } 
    }
    def add(child :Node){ children.append(child) }
}

