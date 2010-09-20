package trigonal.loader
import trigonal.geometry._
import trigonal.scene
import java.io.File
import scala.collection.mutable.ArrayBuffer
import org.lwjgl.BufferUtils


class PmdLoader extends Loader {
    // params
    var version=0.0f
    var name=ByteString("")
    var comment=ByteString("")
    class Vertex(val pos :Vector3, val normal :Vector3, val uv :Vector2,
            val b0 :Int, val b1 :Int, val w0 :Int, val flag :Int)
    val vertices=ArrayBuffer[Vertex]()
    val indices=ArrayBuffer[Int]()
    class Material(val color :RGBA, 
            val specularity :Float, val specular :RGB,
            val ambient :RGB, val toon_index :Int,
            val flag :Int, val VertexCount: Int,
            val texture :ByteString)
    val materials=ArrayBuffer[Material]()
    class Bone(val name :ByteString,
        val parentIndex :Int, val tailIndex :Int,
        val boneType :Int, val ikIndex :Int, val pos :Vector3){
        var englishName=ByteString("")
    }
    val bones=ArrayBuffer[Bone]()
    class IK(val targetIndex :Int, val effectorIndex :Int,
        val iterrations :Int, val weight :Float, val children :Seq[Int])
    val iks=ArrayBuffer[IK]()
    class Morph(val name :ByteString, val morphType :Int,
        val offsets :Seq[(Int, Vector3)]){
        var englishName=ByteString("")
    }
    val morphs=ArrayBuffer[Morph]()
    var englishName=ByteString("")
    var englishComment=ByteString("")
    class BoneGroup(val name :ByteString){
        var englishName=ByteString("")
    }
    val boneGroups=ArrayBuffer[BoneGroup]()
    class ToonTexture{
        var name=ByteString("")
    }
    val toonTextures=for(i <-0 until 10) yield new ToonTexture
    class RigidBody(val name :ByteString, val boneIndex :Int,
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
    class Constraint(val name :ByteString,
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
            val pos=io.getVector3()
            val normal=io.getVector3()
            vertices.append(new Vertex(
                pos, normal, io.getVector2(),
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
        }
        // englishBoneGroup
        for(g <- boneGroups){
            g.englishName=io.getString(50)
        }
        if(io.isEnd){
            return true
        }
        // toonTextures
        for(t <- toonTextures){
            t.name=io.getString(100)
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

    override def build(dir :File) :Any={

        // materials
        val indexArrays=materials.map{
            m => new scene.immutable.IndexArray(
                    BufferUtils.createIntBuffer(m.VertexCount), 
                    //Some(new scene.Material("", m.color))
                    None
                    )
        }

        // index array
        var index=0
        for((m, indexArray)<-materials.zip(indexArrays)){
            for(i <-0 until m.VertexCount){
                indexArray.indices.put(indices(index))
                index+=1
            }
            indexArray.indices.rewind()
        }

        // vertex array
        val positions=BufferUtils.createFloatBuffer(vertices.length*3)
        val uvArray=BufferUtils.createFloatBuffer(vertices.length*2)
        val boneWeights=new Array[(Vector3, Int, Int, Float)](vertices.length)
        for((v, i) <- vertices.zipWithIndex){
            positions.put(v.pos.x)
            positions.put(v.pos.y)
            positions.put(v.pos.z)
            uvArray.put(v.uv.x)
            uvArray.put(v.uv.y)
            boneWeights(i)=(v.pos, v.b0, v.b1, v.w0)
        }
        positions.rewind()
        uvArray.rewind()

        // skeleton, motion
        val boneBuilder=new scene.BoneBuilder("root", Vector3.zero)
        val sceneBones=ArrayBuffer[scene.BoneBuilder]()
        for(b <- bones){
            sceneBones.append(new scene.BoneBuilder(b.name.toString, b.pos))
        }
        for((b, bone) <- bones.zip(sceneBones)){
            if(b.parentIndex==0xFFFF){
                boneBuilder.add(bone)
            }
            else{
                sceneBones(b.parentIndex).add(bone)
            }
        }
        val skeleton=new scene.Skeleton(boneBuilder.result)

        new scene.immutable.SkeletalIndexedVertexArray(
                    positions, uvArray, indexArrays, 
                    boneWeights, skeleton)
    }
}

