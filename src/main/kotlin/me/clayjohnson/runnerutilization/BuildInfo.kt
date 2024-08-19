package me.clayjohnson.runnerutilization

import com.gradle.develocity.api.model.Build
import java.time.Instant

data class BuildInfo(
    val id: String,
    val project: String,
    val buildStarted: Instant,
    val duration: Long,
    val hostname: String) {
    constructor(build: Build) : this(
        id = build.id,
        project = build.project(),
        buildStarted = build.buildStarted(),
        duration = build.buildDuration(),
        hostname = build.hostname()
    )
}

fun Build.project(): String = this.models?.run {
    gradleAttributes?.model?.rootProjectName
        ?: mavenAttributes?.model?.topLevelProjectName
        ?: throw IllegalArgumentException("Build start time is missing.")
}!!

fun Build.buildStarted(): Instant = Instant.ofEpochMilli(this.models?.run {
    gradleAttributes?.model?.buildStartTime
        ?: mavenAttributes?.model?.buildStartTime
        ?: throw IllegalArgumentException("Build start time is missing.")
}!!)

fun Build.buildDuration(): Long = this.models?.run {
    gradleAttributes?.model?.buildDuration
        ?: mavenAttributes?.model?.buildDuration
        ?: throw IllegalArgumentException("Build duration is missing.")
}!!

fun Build.hostname(): String = this.models?.run {
    gradleAttributes?.model?.environment
        ?: mavenAttributes?.model?.environment
        ?: throw IllegalArgumentException("Environment is missing.")
}!!.publicHostname!!