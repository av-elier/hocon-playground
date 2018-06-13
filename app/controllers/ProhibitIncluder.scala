package controllers

import com.typesafe.config._

class ProhibitIncluder extends ConfigIncluder {
    def withFallback(fallback: ConfigIncluder): ConfigIncluder = {
        this
    }
    def include(context: ConfigIncludeContext, what: String): ConfigObject = {
        throw new Exception(s"Includes not allowed here, cannot include $what");
    };
}
