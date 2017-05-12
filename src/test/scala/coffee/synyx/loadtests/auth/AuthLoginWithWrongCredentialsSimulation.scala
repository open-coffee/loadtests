package coffee.synyx.loadtests.auth

import coffee.synyx.loadtests.helper.CoffeeNetConfigFactory
import com.typesafe.config.Config
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AuthLoginWithWrongCredentialsSimulation extends Simulation {

  val conf: Config = CoffeeNetConfigFactory.load("AuthLoginLogoutSimulation")

  val httpConf: HttpProtocolBuilder = http
    .baseURL(conf.getString("baseUrl"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0")

  val scn: ScenarioBuilder = scenario("AuthLoginWithWrongCredentialsSimulation")
    .exec(
      http("GET Auth Login Page")
        .get("/login")
        .check(status.is(200))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
    )
    .feed(csv("user_credentials_wrong.csv").circular)
    .exec(
      http("Login")
        .post("/login")
        .formParam("username", "${username}")
        .formParam("password", "${password}")
        .formParam("_csrf", "${csrfToken}")
        .check(status.is(200))
        .check(css("div.alert.alert-danger").exists)
    )

  setUp(scn.inject(
    rampUsersPerSec(1) to 5 during (0.1 minutes),
    constantUsersPerSec(5) during (0.1 minutes),
    rampUsersPerSec(5) to 1 during (0.1 minutes)
  ))
    .protocols(httpConf)
    .assertions(
      global.responseTime.max.lte(30),
      global.responseTime.mean.lte(15),
      global.successfulRequests.percent.gte(99)
    )
}