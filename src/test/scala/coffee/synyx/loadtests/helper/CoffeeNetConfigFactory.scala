package coffee.synyx.loadtests.helper

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

/**
  * @author Tobias Schneider - schneider@synyx.de
  */
object CoffeeNetConfigFactory {

  /**
    * Merges the following properties into one with the highest order to win
    *
    * external specific configuration -> ${classname}.properties (highest)
    * external default configuration -> application.properties
    * internal default configuration -> application.properties (lowest)
    *
    * The configurations has to be placed where the tests will be started.
    *
    * @return merged configuration
    */
  def load(className: String): Config = {

    val config: Config = ConfigFactory.parseFile(new File(className + ".properties"))
      .withFallback(ConfigFactory.parseFile(new File("application.properties"))
        .withFallback(ConfigFactory.load()))

    println(config.toString)

    config
  }
}
