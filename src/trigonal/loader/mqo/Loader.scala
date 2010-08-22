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
    var diffuseTexture :Option[String]=None
    var alphaTexture :Option[String]=None

    override def toString() :String={
        "<Material"+
        color+
        ">"
    }
}

object Material {
    val materialLine="""^\s*"([^"]+)"(.*)$""".r
    val paramsPattern="""\s*(\w+)\(([^)]+)\)""".r

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
                        case "aplane" => material.alphaTexture=Some(
                            m.group(2).slice(1, m.group(2).length-1))
                        case "tex" => material.diffuseTexture=Some(
                            m.group(2).slice(1, m.group(2).length-1))
                        }
                })
                return material
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
// Face
///////////////////////////////////////////////////////////////////////////////
object Face {
    val faceLine="""^\s*(\d+)(.*)$""".r

    private def parseParams_(count :Int, params :String) 
    :(Array[Int], Int, Array[Float])={
        var indices =new Array[Int](count)
        var material=0
        var uvs =new Array[Float](count*2)
        for(param <- params.split(')')){
            val tokens=param.trim.split(Array('(', ' '))
            tokens match {
                case Array("M", m@_*)=> material=m(0) toInt
                case Array("V", i@_*)=> indices=(i.map(_ toInt)).toArray
                case Array("UV", uv@_*)=> uvs=(uv.map(_ toFloat)).toArray
                case Array("COL", c@_*)=> 0
                case _=> 0
            }
        }
        return (indices, material, uvs)
    }

    def parse(line: String) :Face={
        line match {
            case faceLine(count, params) =>
                val (indices, material, uvs)=parseParams_(count toInt, params)
                val face=indices.length match {
                    case 2=> new Line(indices, uvs)
                    case 3=> new Triangle(indices, uvs)
                    case 4=> new Quadrangle(indices, uvs)
                }
                face.material=material
                return face
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
                case _ => println("unknown key:", tokens(0))
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
// Mqo Loader
///////////////////////////////////////////////////////////////////////////////
class Loader {

    var path=""
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
        "<mqo.Loader"+
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

    def load(src :java.io.File):Boolean=load(src toString)
    def load(src :String):Boolean={
        path=src
        try{
            val iter=Source.fromFile(path, "shift-jis").getLines
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
            while(iter.hasNext){
                iter.next match {
                    case "" => 0
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
            val loader=new Loader
            if(loader.load(arg)){
                println(loader)
            }
            else{
                println("failed!")
            }
        }
    }
}

