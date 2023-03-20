# quartz-h2 JSON https/2 server with jsoniter-scala json codec.

quartz-h2 http2 server.<br>
https://github.com/ollls/quartz-h2

JSON library.<br>
https://github.com/plokhotnyuk/jsoniter-scala

## Debug level in logback-test.xml
For perf tests put debug level to "info", for debuggging use "debug" or "trace"

## Run application

To build as standalone jar 
```shell
sbt assembly
```
To run with sbt
```shell
sbt run
```

## Benchmark ( MacBook 2019 )

```shell
h2load -D10 -c32 -t2 -m18 https://localhost:8443/json

starting benchmark...
spawning thread #0: 16 total client(s). Timing-based test with 0s of warm-up time and 10s of main duration for measurements.
spawning thread #1: 16 total client(s). Timing-based test with 0s of warm-up time and 10s of main duration for measurements.
Warm-up started for thread #1.
Warm-up started for thread #0.
progress: 6% of clients started
...
progress: 87% of clients started
Warm-up phase is over for thread #1.
Main benchmark duration is started for thread #1.
progress: 93% of clients started
progress: 100% of clients started
Warm-up phase is over for thread #0.
Main benchmark duration is started for thread #0.
TLS Protocol: TLSv1.3
Cipher: TLS_AES_256_GCM_SHA384
Server Temp Key: X25519 253 bits
Application protocol: h2
Main benchmark duration is over for thread #1. Stopping all clients.
Stopped all clients for thread #1
Main benchmark duration is over for thread #0. Stopping all clients.
Stopped all clients for thread #0

finished in 10.01s, 64064.00 req/s, 4.58MB/s
requests: 640640 total, 641216 started, 640640 done, 640640 succeeded, 0 failed, 0 errored, 0 timeout
status codes: 640644 2xx, 0 3xx, 0 4xx, 0 5xx
traffic: 45.82MB (48050156) total, 1.22MB (1281672) headers (space savings 94.74%), 33.60MB (35235200) data
                     min         max         mean         sd        +/- sd
time for request:      226us     47.59ms      7.74ms      4.07ms    71.00%
time for connect:    11.42ms     70.69ms     22.77ms     14.83ms    84.38%
time to 1st byte:    13.74ms    109.86ms     30.96ms     20.35ms    87.50%
req/s           :    1958.99     2041.25     2000.73       22.05    65.63%

```
