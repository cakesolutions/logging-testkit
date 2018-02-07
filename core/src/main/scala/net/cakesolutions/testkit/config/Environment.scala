// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.config

/**
  * DevOps environment variable interface
  *
  * Without exception, **all** entries in here should be fully documented!
  */
object Environment {
  /**
    * Required configuration settings are placed here
    */
  object Required {

  }

  /**
    * Optional configuration settings are placed here (these may have sensible
    * default values defined **elsewhere**)
    */
  object Optional {
    /**
      * Duration that we wait for before allowing monitor timeout's to execute.
      */
    val TIMEOUT_DELAY: Option[String] = sys.env.get("TIMEOUT_DELAY")
    /**
      * Periodicity with which monitor timeouts are checked for expiration.
      */
    val TIMEOUT_PERIOD: Option[String] = sys.env.get("TIMEOUT_PERIOD")
  }
}
