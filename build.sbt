ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

ThisBuild / useCoursier := false

lazy val root = (project in file("."))
  .settings(
    name := "udemy-scala-advanced",
    idePackagePrefix := Some("com.hoc081098.udemyscalaadvanced")
  )
