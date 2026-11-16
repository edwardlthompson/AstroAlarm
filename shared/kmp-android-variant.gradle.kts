import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.tasks.bundling.Jar

/**
 * Let an Android :app depend on a KMP module that only publishes jvm()
 * (no androidTarget — this environment often has no Android SDK).
 */
afterEvaluate {
    val jvmJar = tasks.findByName("jvmJar") as? Jar ?: return@afterEvaluate
    val jvmApi = configurations.findByName("jvmApiElements") ?: return@afterEvaluate
    val jvmRuntime = configurations.findByName("jvmRuntimeElements") ?: return@afterEvaluate

    fun org.gradle.api.artifacts.Configuration.copyAttrsFrom(source: org.gradle.api.artifacts.Configuration) {
        source.attributes.keySet().forEach { key ->
            @Suppress("UNCHECKED_CAST")
            val typed = key as Attribute<Any>
            val value = source.attributes.getAttribute(typed) ?: return@forEach
            attributes.attribute(typed, value)
        }
    }

    if (configurations.findByName("androidApiElements") == null) {
        configurations.create("androidApiElements") {
            isCanBeConsumed = true
            isCanBeResolved = false
            copyAttrsFrom(jvmApi)
            attributes.attribute(
                TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                objects.named(TargetJvmEnvironment.ANDROID),
            )
            outgoing.artifact(jvmJar)
        }
        configurations.create("androidRuntimeElements") {
            isCanBeConsumed = true
            isCanBeResolved = false
            copyAttrsFrom(jvmRuntime)
            attributes.attribute(
                TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                objects.named(TargetJvmEnvironment.ANDROID),
            )
            outgoing.artifact(jvmJar)
        }
    }
}
