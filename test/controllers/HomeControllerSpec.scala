package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {
    "render the index page from the router" in {
      // FakeRequest defaults to Host "localhost", which the AllowedHostsFilter rejects;
      // "localhost:9000" is in play.filters.hosts.allowed, so use it explicitly.
      val request = FakeRequest(GET, "/").withHeaders("Host" -> "localhost:9000")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("HOCON")
    }
  }
}
