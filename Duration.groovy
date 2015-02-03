import groovy.io.FileType
import groovy.json.JsonOutput

def files = []
def dir = new File("C:\\Users\\Pablo\\Downloads\\cursos")

def getDuration(file) {

    def out = new StringBuilder()
    def err = new StringBuilder()

    // ffprobe can be found in http://www.ffmpeg.org
    def proc = ['ffprobe', '-i', file.path, '-show_format'].execute()
    proc.waitForProcessOutput(out, err)

    def duration = out.toString().find(/duration=(\w+).(\w+)/).split('=')[1]
    duration.toDouble()
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
        file_name: file.name,
        duration: getDuration(file)
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
