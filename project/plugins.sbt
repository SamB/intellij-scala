addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.0.0")
addSbtPlugin("org.jetbrains" % "sbt-idea-plugin" % "3.11.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.4.6")

libraryDependencies += "io.get-coursier" %% "coursier" % "2.0.16"
