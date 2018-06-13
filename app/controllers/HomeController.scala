package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import com.typesafe.config._

import scala.util.{Failure, Success, Try}

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def hoconToJson() = Action(parse.tolerantText) { implicit request =>
    val resolve = request.queryString.getOrElse("resolve", Seq()).headOption.map(_.toBoolean).getOrElse(false);
    val json = request.queryString.getOrElse("json", Seq()).headOption.map(_.toBoolean).getOrElse(false);
    val formatted = request.queryString.getOrElse("formatted", Seq()).headOption.map(_.toBoolean).getOrElse(false);
    val comments = request.queryString.getOrElse("comments", Seq()).headOption.map(_.toBoolean).getOrElse(false);
    val originComments = request.queryString.getOrElse("originComments", Seq()).headOption.map(_.toBoolean).getOrElse(false);
    val parseOptions = ConfigParseOptions.defaults().setIncluder(new ProhibitIncluder());
    val conf = Try(ConfigFactory.parseString(request.body, parseOptions))
    conf match {
      case Success(conf) => {
        val resolvedConf = if (resolve) conf.resolve() else conf
        val renderConfig = ConfigRenderOptions.defaults()
          .setJson(json)
          .setFormatted(formatted)
          .setComments(comments)
          .setOriginComments(originComments);
        Ok(resolvedConf.root().render(renderConfig))
      }
      case Failure(err) => Ok(err.getMessage)
      case _ => Ok("internal error")
    }
  }
}

