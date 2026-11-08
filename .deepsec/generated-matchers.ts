import { compileDeclarativeMatchers, type DeepsecPlugin } from "deepsec/config";

const specs = [
  {
    "version": 1,
    "slug": "play-hocon-http-surface",
    "description": "Play Framework HOCON playground HTTP surface: route declarations, home controller, HOCON include blocker, and frontend fetch call sites for the public HOCON parsing endpoint.",
    "noiseTier": "normal",
    "filePatterns": [
      "app/controllers/**/*.scala",
      "app/views/**/*.scala.html",
      "public/javascripts/**/*.js",
      "conf/routes"
    ],
    "patterns": [
      {
        "source": "POST\\s+/hoconToJson",
        "flags": "i",
        "label": "HOCON parse route declaration"
      },
      {
        "source": "fetch\\(\\s*[\"'`][^\"'`]*/hoconToJson",
        "flags": "i",
        "label": "Frontend fetch to HOCON endpoint"
      },
      {
        "source": "class\\s+HomeController\\b",
        "flags": "i",
        "label": "Home controller class"
      },
      {
        "source": "class\\s+ProhibitIncluder\\s+extends\\s+ConfigIncluder",
        "flags": "i",
        "label": "HOCON include blocker"
      }
    ],
    "examples": [
      "POST    /hoconToJson                controllers.HomeController.hoconToJson",
      "let r = fetch(`/hoconToJson?${query}`, {",
      "class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {",
      "class ProhibitIncluder extends ConfigIncluder {"
    ],
    "closesSurfaceIds": ["http-playground"]
  }
];

export const generatedMatchersPlugin: DeepsecPlugin = {
  name: "deepsec-generated-matchers",
  matchers: compileDeclarativeMatchers(specs),
};
