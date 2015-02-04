import groovy.io.FileType
import groovy.json.JsonOutput

def getFileDuration(file) {
    def out = new StringBuilder()
    def err = new StringBuilder()

    // ffprobe can be found at http://www.ffmpeg.org
    def proc = ['ffprobe', '-i', file.path, '-show_format'].execute()
    proc.waitForProcessOutput(out, err)

    def duration = out.toString().find(/duration=(\w+).(\w+)/)
    if (duration) {
        duration.split('=')[1].toDouble()
    } else {
        0.0
    }
}

def getExtension(file) {
    def extension = file.name.lastIndexOf('.').with {it != -1 ? file.name[it..<file.name.length()] : "-"}
    extension.toLowerCase()
}

def isVideo(file) {
    getExtension(file) in ['.mp4', '.m4v', '.flv', '.mov', '.avi', '.mpg', '.wmv']
}

def formatTime(millis) {
    def second = (millis / 1000) % 60;
    def minute = (millis / (1000 * 60)) % 60;
    def hour = (millis / (1000 * 60 * 60)) % 24;

    def time = String.format("%02d:%02d:%02d", hour.intValue(), minute.intValue(), second.intValue());
    time
}

def getCourseDuration(course) {
    def duration = 0.0
    course.eachFileRecurse (FileType.FILES) { 
        file -> if (isVideo(file)) {
            duration += getFileDuration(file) 
        }    
    }
    if (duration > 0.0) {
        formatTime(duration * 1000)
    } else {
        '00:00:00'
    }
}

def getFiles(dir) {   
    def files = []
    dir.eachDir {
        school -> school.eachDir {
            course -> files << [
                school: school.name,
                course: course.name,
                duration: getCourseDuration(course)
            ]
        }
    }
    files
}

def writeJsonFile(files) {
    def json = JsonOutput.toJson(files)

    File lista = new File("lista.json")
    lista.withWriter{ out ->
        out.println JsonOutput.prettyPrint(json)
    }    
}

def dir = new File("G:\\cursos")
writeJsonFile(getFiles(dir))
