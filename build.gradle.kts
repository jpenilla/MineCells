import com.matthewprenger.cursegradle.*
import net.fabricmc.loom.task.RemapJarTask
import java.io.FileNotFoundException

plugins {
  id("fabric-loom") version Versions.loom
  id("com.modrinth.minotaur") version Versions.minotaur
  id("com.matthewprenger.cursegradle") version Versions.cursegradle
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

base {
  archivesName.set(ModData.id)
}

group = ModData.group
version = ModData.version

repositories {
  mavenCentral()
  maven(url = "https://jitpack.io")
  maven(url = "https://maven.shedaniel.me/")
  maven(url = "https://maven.terraformersmc.com/releases/")
}

dependencies {
  minecraft("com.mojang:minecraft:${Versions.minecraft}")

  mappings("net.fabricmc:yarn:${Versions.yarn}:v2")

  modImplementation("net.fabricmc:fabric-loader:${Versions.fabricLoader}")
  modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.fabricApi}")

  include("com.github.Draylar.omega-config:omega-config-base:${Versions.omegaConfig}")
  modImplementation("com.github.Draylar.omega-config:omega-config-base:${Versions.omegaConfig}")

  implementation("com.github.LlamaLad7:MixinExtras:${Versions.mixinExtras}")
  annotationProcessor("com.github.LlamaLad7:MixinExtras:${Versions.mixinExtras}")
  include("com.github.LlamaLad7:MixinExtras:${Versions.mixinExtras}")

}

@Suppress("UnstableApiUsage")
tasks {
  withType<ProcessResources> {
    inputs.property("version", ModData.version)
    filesMatching("fabric.mod.json") {
      expand("version" to ModData.version)
    }
  }
  withType<JavaCompile> {
    configureEach {
      options.release.set(17)
    }
  }
  withType<Assemble> {
    dependsOn("runDatagen")
  }
}

// Data generation

tasks.register<Exec>("runPythonDatagen") {
  workingDir = projectDir
  val scriptFile = workingDir.resolve("pyDatagen/main.py")
  val outputPath = workingDir.resolve("src/main/pyGenerated")

  commandLine("python", scriptFile.absolutePath, outputPath.absolutePath)
}

val dataOutput = file("src/main/generated")

loom {
  runs {
    create("datagen")  {
      client()
      name("Data Generation")
      vmArg("-Dfabric-api.datagen")
      vmArg("-Dfabric-api.datagen.output-dir=${dataOutput}")

      runDir("build/datagen")
    }
  }
}

sourceSets {
  main {
    resources {
      srcDirs("src/main/generated", "src/main/pyGenerated")
    }
  }
}

// Publishing
val secretsFile = rootProject.file("publishing.properties")
val secrets = Secrets(secretsFile)

val remapJar = tasks.getByName("remapJar") as RemapJarTask
val newVersionName = "${ModData.id}-${ModData.mcVersions[0]}-${ModData.version}"
val newChangelog = try {
  rootProject.file("changelogs/${ModData.id}_${ModData.version}.md").readText()
} catch (_: FileNotFoundException) {
  println("No changelog found")
  ""
}

if (secrets.isModrinthReady()) {
  println("Setting up Minotaur")
  modrinth {
    token.set(secrets.modrinthToken)
    projectId.set(secrets.modrinthId)
    uploadFile.set(remapJar)
    versionName.set(newVersionName)
    versionType.set(ModData.versionType)
    changelog.set(newChangelog)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    gameVersions.set(ModData.mcVersions)
    loaders.set(listOf("fabric"))
    dependencies {
      ModData.dependencies.forEach(required::project)
    }
  }
}

if (secrets.isCurseforgeReady()) {
  println("Setting up Cursegradle")
  curseforge {
    apiKey = secrets.curseforgeToken
    project(closureOf<CurseProject> {
      id = secrets.curseforgeId
      releaseType = ModData.versionType
      ModData.mcVersions.forEach(::addGameVersion)
      addGameVersion("Fabric")
      changelog = newChangelog
      changelogType = "markdown"
      relations(closureOf<CurseRelation> {
        ModData.dependencies.forEach(::requiredDependency)
      })
      mainArtifact(remapJar, closureOf<CurseArtifact> {
        displayName = newVersionName
      })
    })
    options(closureOf<Options> {
      forgeGradleIntegration = false
    })
  }
  project.afterEvaluate {
    tasks.getByName<CurseUploadTask>("curseforge${secrets.curseforgeId}") {
      dependsOn(remapJar)
    }
  }
}