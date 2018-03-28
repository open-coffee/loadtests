package coffee.synyx.loadtests.auth

import coffee.synyx.loadtests.helper.CoffeeNetConfigFactory
import com.typesafe.config.Config
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AuthLoginLogoutSimulation extends Simulation {

  val conf: Config = CoffeeNetConfigFactory.load("AuthLoginLogoutSimulation")

  val httpConf: HttpProtocolBuilder = http
    .baseURL(conf.getString("baseUrl"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0")

  val scn: ScenarioBuilder = scenario("AuthLoginLogoutSimulation")
    .exec(
      http("GET Auth Login Page")
        .get("/login")
        .check(status.is(200))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
    )
    .feed(csv("user_credentials.csv").circular)
    .exec(
      http("Login")
        .post("/login")
        .formParam("username", "${username}")
        .formParam("password", "${password}")
        .formParam("_csrf", "${csrfToken}")
        .check(status.is(200))
        .check(css("div.alert.alert-danger").notExists)
    )
    .exec(http("Logout")
      .get("/logout")
      .check(status.is(200))
    )

  setUp(scn.inject(
    rampUsersPerSec(1) to 50 during (1 minutes),
    constantUsersPerSec(50) during (2 minutes),
    rampUsersPerSec(50) to 1 during (1 minutes)
  ))
    .protocols(httpConf)
    .assertions(
      global.successfulRequests.percent.gte(99)
    )
}
