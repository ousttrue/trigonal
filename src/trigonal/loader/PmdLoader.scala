package trigonal.loader
import trigonal.geometry._
import trigonal.scene
import java.io.File
import scala.collection.mutable.ArrayBuffer
import org.lwjgl.BufferUtils


class PmdLoader extends Loader {

    // params
    var version=0.0f
    var name=""
    var comment=""
    class Vertex(val pos :Vector3, val normal :Vector3, val uv :Vector2,
            b0 :Int, b1 :Int, w0 :Int, flag :Int)
    val vertices=ArrayBuffer[Vertex]()
    val indices=ArrayBuffer[Int]()
    class Material(val color :RGBA, 
            val specularity :Float, val specular :RGB,
            val ambient :RGB, val toon_index :Int,
            val flag :Int, val VertexCount: Int,
            val texture :String)
    val materials=ArrayBuffer[Material]()
    class Bone(val name :String,
        val parentIndex :Int, val tailIndex :Int,
        val boneType :Int, val ikIndex :Int, val pos :Vector3){
        var englishName=""
    }
    val bones=ArrayBuffer[Bone]()
    class IK(val targetIndex :Int, val effectorIndex :Int,
        val iterrations :Int, val weight :Float, val children :Seq[Int])
    val iks=ArrayBuffer[IK]()
    class Morph(val name :String, val morphType :Int,
        val offsets :Seq[(Int, Vector3)]){
        var englishName=""
    }
    val morphs=ArrayBuffer[Morph]()
    var englishName=""
    var englishComment=""
    class BoneGroup(val name :String){
        var englishName=""
    }
    val boneGroups=ArrayBuffer[BoneGroup]()
    class ToonTexture{
        var name=""
    }
    val toonTextures=for(i <-0 until 10) yield new ToonTexture
    class RigidBody(val name :String, val boneIndex :Int,
        val group :Int, val collision :Int,
        val shape :Int, val width :Float, val height :Float, val depth :Float,
        val pos :Vector3, val rot :Vector3,
        val weight :Float, 
        val a :Float,
        val b :Float,
        val c :Float,
        val d :Float,
        val rigidBodyType :Int)
    val rigidBodies=ArrayBuffer[RigidBody]()
    class Constraint(val name :String,
        val rigidBodyA :Int, val rigidBodyB :Int,
        val pos :Vector3, val rot :Vector3,
        val moveLimit1 :Vector3, val moveLimit2 :Vector3,
        val rotLimit1 :Vector3, val rotLimit2 :Vector3,
        val springPos :Vector3, val springRot :Vector3)
    val constraints=ArrayBuffer[Constraint]()

    override def load(path :File) :Boolean={
        val io=new ByteReader(path)

        val sig=io.getString(3)
        if(sig!="Pmd"){
            System.out.println("invalid signature: ["+sig+"]")
                return false
        }

        version=io.getFloat()
        name=io.getString(20)
        comment=io.getString(256)

        // vertices
        val vertexCount=io.getDWORD()
        for(i <- 0 until vertexCount){
            vertices.append(new Vertex(
                io.getVector3(), io.getVector3(), io.getVector2(),
                io.getWORD(), io.getWORD(), io.get(), io.get()
                ))
        }
        // faces
        val indexCount=io.getDWORD()
        for(i <-0 until indexCount){
            indices.append(io.getWORD())
        }
        // materials
        val materialCount=io.getDWORD()
        for(i <-0 until materialCount){
            materials.append(new Material(
                io.getRGBA(), io.getFloat(), io.getRGB(), io.getRGB(),
                io.get(), io.get(), io.getDWORD(), io.getString(20)
            ))
        }
        // bones
        val boneCount=io.getWORD()
        for(i <-0 until boneCount){
            bones.append(new Bone(
                io.getString(20), io.getWORD(), io.getWORD(),
                io.get(), io.getWORD(), io.getVector3()
            ))
        }
        // IK
        val ikCount=io.getWORD()
        for(i <-0 until ikCount){
            val targetIndex=io.getWORD()
            val effectorIndex=io.getWORD()
            val length=io.get()
            iks.append(new IK(targetIndex, effectorIndex, 
                io.getWORD(), io.getFloat(),
                for(i <-0 until length) yield io.getWORD()
            ))
        }
        // morph
        val morphCount=io.getWORD()
        for(i <-0 until morphCount){
            val name=io.getString(20)
            val vertexCount=io.getDWORD()
            morphs.append(new Morph(
                name, io.get(), 
                for(i <-0 until vertexCount) 
                    yield (io.getDWORD() -> io.getVector3())
            ))
        }
        // morphOrder
        val morphOrder=io.get()
        for(i <-0 until morphOrder){
            io.getWORD()
        }
        // boneGroup
        val boneGroupCount=io.get()
        for(i <-0 until boneGroupCount){
            boneGroups.append(new BoneGroup(io.getString(50)))
        }
        // boneGroupAssign
        val boneGroupAssign=io.getDWORD()
        for(i <-0 until boneGroupAssign){
            io.getWORD()
            io.get()
        }
        // extension
        val extend=io.get()
        if(extend==0){
            println("no extension")
            return true
        }
        englishName=io.getString(20)
        englishComment=io.getString(256)
        // englishBone
        for(b <- bones){
            b.englishName=io.getString(20)
        }
        // englishMorph
        for(m <- morphs.tail){
            m.englishName=io.getString(20)
            println(m.englishName)
        }
        // englishBoneGroup
        for(g <- boneGroups){
            g.englishName=io.getString(50)
            println(g.englishName)
        }
        if(io.isEnd){
            return true
        }
        // toonTextures
        for(t <- toonTextures){
            t.name=io.getString(100)
            println(t.name)
        }
        if(io.isEnd){
            println("no rigid bodies")
            return true
        }
        // rigid bodies
        val rigidBodyCount=io.getDWORD()
        for(i <-0 until rigidBodyCount){
            rigidBodies.append(new RigidBody(io.getString(20),
                io.getWORD(), io.get(), io.getWORD(), io.get(),
                io.getFloat(), io.getFloat(), io.getFloat(),
                io.getVector3(), io.getVector3(),
                io.getFloat(), io.getFloat(), io.getFloat(), io.getFloat(),
                io.getFloat(), io.get()
                ))
        }
        // constraints
        val constraintCount=io.getDWORD()
        for(i <-0 until constraintCount){
            constraints.append(new Constraint(io.getString(20),
                io.getDWORD(), io.getDWORD(),
                io.getVector3(), io.getVector3(),
                io.getVector3(), io.getVector3(),
                io.getVector3(), io.getVector3(),
                io.getVector3(), io.getVector3()
                ))
        }
        //
        assert(io.isEnd)
        return true
    }

    override def toString() :String="<Pmd %s %dvertices>".format(
        englishName, vertices.length)

    override def accept(path :File)=path.toString.toLowerCase.endsWith(".pmd")

    override def build(dir :File) :Option[scene.Node]={

        val indexArrays=materials.map{
            m => new scene.immutable.IndexArray(
                    BufferUtils.createIntBuffer(m.VertexCount), 
                    Some(new scene.Material("", m.color)))
        }

        var index=0
        for((m, indexArray)<-materials.zip(indexArrays)){
            for(i <-0 until m.VertexCount){
                indexArray.indices.put(indices(index))
                index+=1
            }
            indexArray.indices.rewind()
        }

        val positions=BufferUtils.createFloatBuffer(vertices.length*3)
        val uvArray=BufferUtils.createFloatBuffer(vertices.length*2)
        for(v <- vertices){
            positions.put(v.pos.x)
            positions.put(v.pos.y)
            positions.put(v.pos.z)
            uvArray.put(v.uv.x)
            uvArray.put(v.uv.y)
        }
        positions.rewind()
        uvArray.rewind()
        Some(new scene.immutable.IndexedVertexArray(
                    positions, uvArray, indexArrays))
    }
}

