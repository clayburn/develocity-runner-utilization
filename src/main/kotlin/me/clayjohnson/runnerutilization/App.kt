package me.clayjohnson.runnerutilization

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.gradle.develocity.api.BuildsApi
import com.gradle.develocity.api.client.ApiClient
import java.time.Instant

fun main(args: Array<String>) = App().main(args)

class App : CliktCommand() {
    private val apiKey by option(envvar = "API_KEY").required()
    private val develocityUrl by option(envvar = "DEVELOCITY_URL").required()
    private val reportFile by option(envvar = "REPORT_FILE").required()
    private val projectName by option(envvar = "PROJECT_NAME").required()
    private val days by option().int().default(28)

    override fun run() {
        val now = Instant.now()

        val buildsApi = BuildsApi().apply {
            apiClient = ApiClient().apply {
                basePath = develocityUrl
                setBearerToken(apiKey)
            }
        }

        val query = "project:$projectName"

        val builds = BuildsProcessor(
            buildsApi = buildsApi,
            buildsQuery = query
        ).process(now.daysAgo(days)).map { BuildInfo(it) }


        CsvCreator().createReport(builds, reportFile)
    }
}

