import gg.meza.stonecraft.mod
import java.net.URI
import java.nio.file.Files

plugins {
    id("gg.meza.stonecraft")
	id("com.ezzenix.mcverify") version "0.1.0"
	id("maven-publish")
}

val isDeobfuscated = stonecutter.current.parsed >= "26.1"

modSettings {
    clientOptions {
        fov = 90
        guiScale = 2
        narrator = false
        darkBackground = true
        musicVolume = 0.0
    }
}

fun fetchLatestChangelog() : String {
	val str = Files.readString(layout.settingsDirectory.file("CHANGELOG.md").asFile.toPath())
	val first = str.indexOf("## ")
	val i = str.indexOf('\n', first) + 2
	var r = str.indexOf("\n## ", i + 1)
	if (r == -1) r = str.length
	return str.substring(i, r - 1)
}

repositories {
    maven { name = "Terraformers"; url = URI("https://maven.terraformersmc.com/") }
}

dependencies {
	val implementationConfiguration = when {
		isDeobfuscated -> "implementation"
		else -> "modImplementation"
	}
	val apiConfiguration = when {
		isDeobfuscated -> "api"
		else -> "modApi"
	}

	compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
	annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
	if (mod.isForge) {
		implementation("io.github.llamalad7:mixinextras-forge:0.5.4")
		include("io.github.llamalad7:mixinextras-forge:0.5.4")
	}
	if (mod.isNeoforge) {
		implementation("io.github.llamalad7:mixinextras-neoforge:0.5.4")
		include("io.github.llamalad7:mixinextras-neoforge:0.5.4")
	}

	if (mod.isFabric && mod.hasProp("deps.modmenu")) {
		add(implementationConfiguration, "com.terraformersmc:modmenu:${mod.prop("deps.modmenu")}")
	}
}

loom {
	if (mod.isForge) {
		forge {
			mixinConfig("${mod.id}.mixins.json")
		}
	}
}

gradle.projectsEvaluated {
	allprojects.filter { it.tasks.names.contains("runClient") }.forEach { project ->
		tasks.register("Run ${project.name}") {
			dependsOn(project.tasks.named("runClient"))
			group = "runs"
		}
	}
}

mcverify {
	loader = mod.loader
	if (mod.hasProp("supported_to")) {
		versionRange {
			start = mod.minecraftVersion
			end = mod.prop("supported_to")
		}
	} else {
		version = mod.minecraftVersion
	}
}

tasks.named("publishToMavenLocal") {
	dependsOn(tasks.build)
}

publishing {
	repositories {
		maven {
			name = "pages"
			url = uri(rootProject.layout.buildDirectory.dir("repo"))
		}
	}
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "com.ezzenix"
			artifactId = "emlib"
			version = "${mod.version}+${mod.minecraftVersion}-${mod.loader}-SNAPSHOT"

			afterEvaluate {
				val remapJarProvider = tasks.matching { it.name == "remapJar" }.singleOrNull()
				if (remapJarProvider != null) {
					artifact(remapJarProvider)
				} else {
					from(components["java"])
				}
			}
		}
	}
}
