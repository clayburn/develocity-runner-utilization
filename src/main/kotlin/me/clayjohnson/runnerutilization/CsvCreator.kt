package me.clayjohnson.runnerutilization

import java.io.File

class CsvCreator {
    fun createReport(builds: List<BuildInfo>, csvFile: String) {
        val csv = File(csvFile)
        csv.delete()
        csv.parentFile.mkdirs()
        csv.printWriter().use { out ->
            out.println("ID,Project Name,Hostname,Build Started (UTC),Build Duration (ms)")
            builds.forEach { build ->
                out.println("${build.id},${build.project},${build.hostname},${build.buildStarted},${build.duration}")
            }
        }
    }
}