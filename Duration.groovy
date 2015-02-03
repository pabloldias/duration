import groovy.io.FileType
import groovy.json.JsonOutput

def files = []
def dir = new File("my_dir")

def getMetadata(file) {
    def out = new StringBuilder()
    def err = new StringBuilder()
    def proc = ['ffprobe', '-i', file.path, '-show_format'].execute()
    proc.waitForProcessOutput(out, err)
    out.toString()
}

def getExtension(file) {
    def extension = file.name.lastIndexOf('.').with {it != -1 ? file.name[it..<file.name.length()] : "-"}
    extension
}

def isVideo(file) {
    getExtension(file) in ['.mp4']
}


def addFile(file) {
    [
        fileName: file.name,
        duration: file.size(),
        metadata: getMetadata(file),
    ]    
}

def writeJsonFile(files) {
    def json = JsonOutput.toJson(files)

    File lista = new File("lista.json")
    lista.withWriter{ out ->
        out.println JsonOutput.prettyPrint(json)
    }    
}


dir.eachFileRecurse (FileType.FILES) { file -> 
    if (isVideo(file)) {
        files << addFile(file) 
    }    
}

writeJsonFile(files)
