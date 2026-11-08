package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import com.typesafe.config._

import scala.util.{Failure, Success, Try}
import java.util.concurrent.ConcurrentHashMap

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  val confResolverOptions = ConfigResolveOptions.noSystem()

  private val MaxHoconInputBytes = 100 * 1024L

  private val RateLimitMaxRequests = 30
  private val RateLimitWindowMs = 60 * 1000L
  private val rateLimitBuckets = new ConcurrentHashMap[String, Array[Long]]()

  private def rateLimitExceeded(remoteAddress: String): Boolean = {
    val now = System.currentTimeMillis()
    val bucket = rateLimitBuckets.compute(remoteAddress, (_, current) => {
      if (current == null || now - current(0) >= RateLimitWindowMs) Array(now, 1L)
      else {
        current(1) += 1
        current
      }
    })
    bucket(1) > RateLimitMaxRequests
  }

  def hoconToJson() = Action(parse.tolerantText(maxLength = MaxHoconInputBytes)) { implicit request =>
    if (rateLimitExceeded(request.remoteAddress)) {
      TooManyRequests("Rate limit exceeded. Please try again later.")
    } else {
      val resolve = request.queryString.getOrElse("resolve", Seq()).headOption.map(_.toBoolean).getOrElse(false);
      val json = request.queryString.getOrElse("json", Seq()).headOption.map(_.toBoolean).getOrElse(false);
      val formatted = request.queryString.getOrElse("formatted", Seq()).headOption.map(_.toBoolean).getOrElse(false);
      val comments = request.queryString.getOrElse("comments", Seq()).headOption.map(_.toBoolean).getOrElse(false);
      val originComments = request.queryString.getOrElse("originComments", Seq()).headOption.map(_.toBoolean).getOrElse(false);
      val parseOptions = ConfigParseOptions.defaults().setIncluder(new ProhibitIncluder());
      val conf = Try(ConfigFactory.parseString(request.body, parseOptions))

      conf match {
        case Success(conf) => {
          val resolvedConf = if (resolve) conf.resolve(confResolverOptions) else conf
          val renderConfig = ConfigRenderOptions.defaults()
            .setJson(json)
            .setFormatted(formatted)
            .setComments(comments)
            .setOriginComments(originComments);
          val rendered = resolvedConf.root().render(renderConfig)
          Ok(rendered)
        }
        case Failure(err) => {
          Logger(getClass).error("Failed to parse user-supplied HOCON input", err)
          Ok("Invalid HOCON input")
        }
        case _ => Ok("internal error")
      }
    }
  }
}

