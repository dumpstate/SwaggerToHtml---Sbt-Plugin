import sbt._
import sbt.Keys._

import java.io.IOException

import com.wordnik.swagger.codegen.{ClientOptInput, ClientOpts, CodegenConfig, DefaultGenerator}
import com.wordnik.swagger.codegen.languages.StaticHtmlGenerator
import com.wordnik.swagger.models.auth.AuthorizationValue
import io.swagger.parser.SwaggerParser

case class InputFileDoesNotExist(msg: String) extends IOException(msg)

object SwaggerToHtmlPlugin extends AutoPlugin {
  lazy val swaggerToHtml = taskKey[Unit]("Generates HTML from Swagger spec.")

  lazy val swaggerToHtmlInput = settingKey[File]("Swagger spec file.")
  lazy val swaggerToHtmlOutput = settingKey[File]("Output file.")

  private def config(out: String) = {
    val conf = new StaticHtmlGenerator()
    conf setOutputDir out
    conf
  }

  private def input(out: String) = {
    val input = new ClientOptInput()
    input setConfig config(out)
    input
  }

  private def swagger(in: String) = new SwaggerParser().read(in)

  private def generator(in: String, out: String) =
    new DefaultGenerator()
      .opts(input(out).opts(new ClientOpts())
        .swagger(swagger(in)))

  override def projectSettings = Seq(
    swaggerToHtmlInput := baseDirectory.value / "doc" / "swagger.yaml",
    swaggerToHtmlOutput := baseDirectory.value / "doc" / "swagger-index.html",
    swaggerToHtml := {
      swaggerToHtmlInput.value exists match {
        case true =>
          val in = swaggerToHtmlInput.value.getAbsolutePath
          val out = swaggerToHtmlOutput.value.getAbsolutePath
          generator(in, out).generate()
        case false =>
          throw InputFileDoesNotExist(s"Input file: ${swaggerToHtmlInput.value} doesn't exist.")
      }
    }
  )
}
