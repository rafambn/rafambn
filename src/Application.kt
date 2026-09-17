package com.rafambn.profilebanner

import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.logging.AppScribe
import com.rafambn.profilebanner.web.configureRoutes
import com.rafambn.scribe.seal
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.event.Level
import java.nio.file.Path

fun Application.module() {
    val config = environment.config
    val databasePath = Path.of(config.property("profileBanner.databasePath").getString().trim())
    val logFilePath = Path.of(config.property("profileBanner.logFilePath").getString().trim())
    AppScribe.configure(logFilePath)
    AppScribe.hire()

    val views = ViewStore(databasePath)
    configureRoutes(views)

    monitor.subscribe(ApplicationStopped) {
        try {
            views.close()
        } catch (error: Exception) {
            if (AppScribe.isEnabled("Application", Level.ERROR, null)) {
                val scroll = AppScribe.newScroll()
                scroll["level"] = JsonPrimitive(Level.ERROR.name)
                scroll["logger"] = JsonPrimitive("Application")
                scroll["message"] = JsonPrimitive("Could not close view store")
                scroll["exception"] = JsonPrimitive(error.stackTraceToString())
                scroll.seal(AppScribe)
            }
        }
    }
}
