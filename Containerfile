FROM docker.io/sbtscala/scala-sbt:eclipse-temurin-focal-11.0.22_7_1.9.9_3.4.0
WORKDIR /hocon-playground
COPY . /hocon-playground
RUN sbt version
RUN sbt stage
CMD [ "target/universal/stage/bin/hocon-playground" ]
