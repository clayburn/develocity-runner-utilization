package me.clayjohnson.runnerutilization

import com.gradle.develocity.api.BuildsApi
import com.gradle.develocity.api.model.Build
import com.gradle.develocity.api.model.BuildModelName
import com.gradle.develocity.api.model.BuildsQuery
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Instant
import java.util.function.Consumer

private val logger = KotlinLogging.logger {}

class BuildsProcessor(
    private val buildsApi: BuildsApi,
    private val buildsQuery: String,
    private val maxWaitSeconds: Int,
) {
    fun process(fromInstant: Instant) : List<Build> {
        val builds = mutableListOf<Build>()
        var fromApplicator = Consumer {
            buildsQuery: BuildsQuery -> buildsQuery.fromInstant(fromInstant.toEpochMilli())
        }

        logger.debug { "maxWaitSeconds: $maxWaitSeconds" }

        while (true) {
            val query = BuildsQuery().apply {
                reverse = false
                maxWaitSecs = maxWaitSeconds
                models = mutableListOf(BuildModelName.GRADLE_ATTRIBUTES, BuildModelName.MAVEN_ATTRIBUTES)
                query = buildsQuery
            }
            fromApplicator.accept(query)

            logger.debug { "Querying next set of builds..." }
            val result = buildsApi.getBuilds(query)

            if (result.isEmpty()) {
                logger.debug { "Empty result returned. Exiting BuildsProcessor." }
                break
            } else {
                logger.debug { "Query returned ${result.size} results (${result.size + builds.size} total so far)" }
            }

            builds.addAll(result)
            fromApplicator = Consumer {
                    buildsQuery: BuildsQuery -> buildsQuery.fromBuild(result.last().id)
            }
        }

        return builds.toList()
    }
}