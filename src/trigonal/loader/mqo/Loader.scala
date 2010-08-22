package trigonal.loader.mqo
import trigonal.geometry._

import java.io.File
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

///////////////////////////////////////////////////////////////////////////////
// Material
///////////////////////////////////////////////////////////////////////////////
class Material(var name :String) {
    var shader=3
    var color=new RGBA(0.5f, 0.5f, 0.5f, 1.0f)
    var diffuse=1.0
    var ambient=0.25
    var emit=0.25
    var specular=0.0
    var power=5.0
    var useVertexColor=0
    var diffuseTexture=""
    var alphaTexture=""

    override def toString() :String={
        "<Material"+
        color+
        ">"
    } 
}

object Material {
    val materialLine="""^\s"([^"]+)"(.*)$""".r
    val paramsPattern="""^\s*(\w+)\(([^)]+)\)$""".r

    def parse(line: String) :Material={
        line match {
            case materialLine(name, params) =>
                val material=new Material(name)
                paramsPattern.findAllIn(params).matchData.foreach(m => {
                        m.group(1) match {
                        case "col" => 
                        val t=m.group(2).split(" ")
                        material.color=RGBA(t(0), t(1), t(2), t(3))
                        case "shader" => material.shader=m.group(2) toInt
                        case "dif" => material.diffuse=m.group(2) toFloat
                        case "amb" => material.ambient=m.group(2) toFloat
                        case "emi" => material.emit=m.group(2) toFloat
                        case "spc" => material.specular=m.group(2) toFloat
                        case "power" => material.power=m.group(2) toFloat
                        case "vcol" => material.useVertexColor=m.group(2) toInt
                        case "aplane" => material.alphaTexture=m.group(2)
                        case "tex" => material.diffuseTexture=m.group(2)
                        }
                })
                return material
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
// Face
///////////////////////////////////////////////////////////////////////////////
object Line {
    def parse(params :String) :Line={
        var line=new Line
        for(param <- params.split(')')){
            val tokens=param.trim.split(Array('(', ' '))
            tokens match {
                case Array("V", i0, i1)=>
                    line=new Line(i0 toInt, i1 toInt, line)
                case Array("M", m)=>
                    line.material=m.toInt
                case Array("UV", u0, v0, u1, v1)=>
                    line.uv(0)=Vector2(u0, v0)
                    line.uv(1)=Vector2(u1, v1)
                case Array("COL", c0, c1, c2)=>
                    line;
            }
        }
        return line
    }
}

object Triangle {
    def parse(params :String) :Triangle={
        var triangle=new Triangle
        for(param <- params.split(')')){
            val tokens=param.trim.split(Array('(', ' '))
            tokens match {
                case Array("V", i0, i1, i2)=>
                    triangle=new Triangle(
                            i0 toInt, i1 toInt, i2 toInt, triangle)
                case Array("M", m)=>
                    triangle.material=m.toInt
                case Array("UV", u0, v0, u1, v1, u2, v2)=>
                    triangle.uv(0)=Vector2(u0, v0)
                    triangle.uv(1)=Vector2(u1, v1)
                    triangle.uv(2)=Vector2(u2, v2)
                case Array("COL", c0, c1, c2)=>
                    triangle;
            }
        }
        return triangle
    }
}

object Quadrangle {
    def parse(params :String) :Quadrangle={
        var quadrangle=new Quadrangle
        for(param <- params.split(')')){
            val tokens=param.trim.split(Array('(', ' '))
            tokens match {
                case Array("V", i0, i1, i2, i3)=>
                    quadrangle=new Quadrangle(
                            i0 toInt, i1 toInt, i2 toInt, i3 toInt, quadrangle)
                case Array("M", m)=>
                    quadrangle.material=m.toInt
                case Array("UV", u0, v0, u1, v1, u2, v2, u3, v3)=>
                    quadrangle.uv(0)=Vector2(u0, v0)
                    quadrangle.uv(1)=Vector2(u1, v1)
                    quadrangle.uv(2)=Vector2(u2, v2)
                    quadrangle.uv(3)=Vector2(u3, v3)
                case Array("COL", c0, c1, c2, c3)=>
                    quadrangle;
            }
        }
        return quadrangle
    }
}

object Face {
    val faceLine="""^\s*(\d+)(.*)$""".r

    def parse(line: String) :Face={
        line match {
            case faceLine(count, params) =>
                count match {
                    case "2" => return Line.parse(params)
                    case "3" => return Triangle.parse(params)
                    case "4" => return Quadrangle.parse(params)
                }
        }
    }
}
                
///////////////////////////////////////////////////////////////////////////////
// Object
///////////////////////////////////////////////////////////////////////////////
class Object(var name :String) {
    var depth=0
    var folding=1
    var scale=Vector3(0, 0, 0)
    var rotation=Vector3(0, 0, 0)
    var translation=Vector3(0, 0, 0)
    var visible=0
    var locking=0
    var shading=0
    var facet=0.0f
    var color=RGBA(0.5f, 0.5f, 0.5f, 1.0f)
    var color_type=0
    val vertices=new ArrayBuffer[Vector3]
    val faces=new ArrayBuffer[Face]

    override def toString() :String={
        "["+name+
        " vertices: "+vertices.size+
        ", faces: "+faces.size+
        "]"
    }
}

object Object{
    def parse(name: String, iter :Iterator[String]) :Object={
        val obj=new Object(name)
        for(line <- iter){
            val l=line.trim
            if(l=="}"){
                return obj
            }
            val tokens=l.split(" ")
            //println(tokens(0))
            tokens match {
                case Array("depth", value) => obj.depth=value toInt
                case Array("folding", value) => obj.folding=value toInt
                case Array("scale", x, y, z) => obj.scale=Vector3(x, y, z)
                case Array("rotation", x, y, z) => obj.rotation=Vector3(x, y, z)
                case Array("translation", x, y, z) => 
                obj.translation=Vector3(x, y, z)
                case Array("visible", value)=> obj.visible=value toInt
                case Array("locking", value)=> obj.locking=value toInt
                case Array("shading", value)=> obj.shading=value toInt
                case Array("facet", value)=> obj.facet=value toFloat
                case Array("color", r, g, b)=> obj.color=RGBA(r, g, b, "1")
                case Array("color_type", value)=> obj.color_type=value toInt
                case Array("vertex", count, "{")=> 
                Object.parseVertices(obj, iter, count toInt)
                case Array("face", count, "{")=> 
                Object.parseFaces(obj, iter, count toInt)
            }
        }
        return null
    }

    def parseVertices(obj :Object, iter :Iterator[String], count :Int){
        for(line <- iter){
            val l=line.trim
            if(l=="}"){
                return
            }
            l.split(" ") match {
                case Array(x, y, z)=> obj.vertices.append(Vector3(x, y, z))
            }
        }
    }

    def parseFaces(obj :Object, iter :Iterator[String], count :Int){
        for(line <- iter){
            val l=line.trim
            if(l=="}"){
                return
            }
            obj.faces.append(Face.parse(l))
        }
    }

}

///////////////////////////////////////////////////////////////////////////////
// Loader
///////////////////////////////////////////////////////////////////////////////
class Loader {

    var pos=Vector3(0, 0, 0)
    var lookat=Vector3(0, 0, 0)
    var head=0.0
    var pitch=0.0
    var orthogonal=false
    var zoom2=0.0
    var amb=Vector3(0, 0, 0)
    val materials=new ArrayBuffer[Material]
    val objects=new ArrayBuffer[Object]

    override def toString() :String={
        "<Loader"+
        " materials: "+materials.size+
        ", objects: "+objects.size+"\n"+
        (for(o <- objects)yield o.toString).mkString("\n") +
        ">"
    }

    private def _parseScene(iter :Iterator[String]) {
        for(line <- iter){
            if(line=="}"){
                return true;
            }
            val tokens=line.trim.split(" ")
                tokens match {
                case Array("pos", x, y, z)=> pos=Vector3(x, y, z)
                case Array("lookat", x, y, z)=> lookat=Vector3(x, y, z)
                case Array("head", value)=> head=value toFloat
                case Array("pich", value)=> pitch=value toFloat
                case Array("ortho", value)=> orthogonal=(value.toInt != 0)
                case Array("zoom2", value)=> zoom2=value toFloat
                case Array("amb", x, y, z)=> amb=Vector3(x, y, z)
                case _ => println("unknown key:", tokens(0))
            }
        }
    }

    private def _parseMaterials(iter :Iterator[String], count: Int){
        for(line <- iter){
            if(line=="}"){
                return true
            }
            materials.append(Material.parse(line));
        }
    }

    def load(path :String):Boolean={
        try{
            val iter=Source.fromFile(path).getLines
            // check first line
            if(iter.next!="Metasequoia Document"){
                return false
            }
            // check second line
            if(iter.next!="Format Text Ver 1.0"){
                return false;
            }
            // each line
            val materialChunk="""Material (\d+) \{""".r
            val objectChunk="""^Object "([^"]+)" \{""".r
            for(line <- iter if line!=""){
                line match {
                    case "Eof" => return true
                    case "Scene {" => _parseScene(iter)
                    case materialChunk(count) => _parseMaterials(
                            iter, count toInt)
                    case objectChunk(name) => 
                        objects.append(Object.parse(name, iter))
                    case default => println("unknown chunk:", default)
                }
            }
            return false;
        }
        catch {
            case ex :java.io.FileNotFoundException =>
                     println(ex toString)
            return false;
        }
    }
}

object Loader {
    def main(args :Array[String]){
        for(arg <- args){
            println("load: "+arg)
            val loader=new Loader()
            if(loader.load(arg)){
                println(loader)
            }
            else{
                println("failed!")
            }
        }
    }
}

