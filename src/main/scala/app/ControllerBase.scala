package app

import model.Account
import org.scalatra._
import org.scalatra.json._
import org.json4s._
import jp.sf.amateras.scalatra.forms._
import service._

/**
 * Provides generic features for ScalatraServlet implementations.
 */
abstract class ControllerBase extends ScalatraFilter with ClientSideValidationFormSupport with JacksonJsonSupport {

  implicit val jsonFormats = DefaultFormats

  /**
   * Returns the context object for the request.
   */
  implicit def context: Context = Context(servletContext.getContextPath, LoginAccount)
  
  private def LoginAccount: Option[Account] = {
    session.get("LOGIN_ACCOUNT") match {
      case Some(x: Account) => Some(x)
      case _ => None
    }
  }

  protected def NotFound()     = html.error("Not Found")
  protected def Unauthorized() = redirect("/")

  protected def baseUrl = {
    val url = request.getRequestURL.toString
    url.substring(0, url.length - request.getRequestURI.length)
  }

  protected def identifier: Constraint = new Constraint(){
    def validate(name: String, value: String): Option[String] =
      if(!value.matches("^[a-zA-Z0-9\\-_]+$")){
        Some("%s contains invalid character.".format(name))
      } else if(value.startsWith("_") || value.startsWith("-")){
        Some("%s starts with invalid character.".format(name))
      } else {
        None
      }
  }

}

case class Context(path: String, loginAccount: Option[Account])