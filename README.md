# Simple example with http/2 quartz-h2 server/client with jsoniter-scala json codec accessing Chat GPT API.

quartz-h2 http2 server.<br>
https://github.com/ollls/quartz-h2

JSON library.<br>
https://github.com/plokhotnyuk/jsoniter-scala

## Commands:

- ```sbt run```
http POST english text to https://127.0.0.1:8443/token.

 - ```sbt assembly```
 ```java -jar qh2-http-run.jar```

 - ```sbt test```
Make sure you have proper path for
```scala
  val FOLDER_PATH = "...."
  val BIG_FILE = "img_0278.jpeg"
```
Options for logging level.
```
sbt "run --debug"
sbt "run --error"
sbt "run --off"
```

