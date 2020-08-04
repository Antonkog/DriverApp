package dependencies

class Versions(val major: String,val minor:String,val rev:String,val offset:String) {
    val androidCompileSdkVersion = 29
    val androidMinSdkVersion = 23

    public var versionMajor = 1//envrMajor
    public var versionMinor = 4    // Release gerade Zahlen im PlayStore
    public var versionRevision = 8
    public var versionOffset = 0
    public var androidVersionCode = getVersionCode(this.versionMajor,this.versionMinor,this.versionRevision,this.versionOffset)
    public var androidVersionName = getVersionName(this.versionMajor,this.versionMinor,this.versionRevision)
    fun getVersionCode(major: Int, minor:Int, rev:Int, offset:Int):Int {
       return (major * 10000 + minor * 100 + rev) * 100 + offset
    }
    fun getVersionCode(major: String, minor:String, rev:String, offset:String):Int {
       return getVersionCode(major.toInt(),minor.toInt(),rev.toInt(),offset.toInt())
    }

    fun getVersionName(major: Int, minor:Int, rev:Int):String{
	return "$major.$minor.$rev"
    }

    init {
    	this.versionMajor=major.toInt()
     	this.versionMinor=minor.toInt()
	this.versionRevision=rev.toInt()
	this.versionOffset=offset.toInt()
	androidVersionCode=this.getVersionCode(major,minor,rev,offset)
        androidVersionName=this.getVersionName(this.versionMajor,this.versionMinor,this.versionRevision)	
    }

}