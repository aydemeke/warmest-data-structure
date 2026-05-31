# Warmest Data Structure

Stores `String -> int` entries and tracks the most recently accessed key (put or
get counts), all in O(1).

Java + Spring Boot home exercise, three parts: data structure, REST API, three
instances sharing state.

## Running it

JDK 17.

```bash
./mvnw clean install         # build + run tests
./mvnw spring-boot:run       # single instance on :8080
docker compose up --build    # Redis + 3 apps on :8080, :8081, :8082
```

## Part 1 — the data structure

LRU-style: `HashMap<String, Node>` plus a doubly linked list with two sentinels.

- map → O(1) lookup by key
- list → access order; warmest is `head.next`
- put / get → unlink the node, push it to the head. Pointer writes only, no traversal.
- get on a missing key returns null and doesn't touch the list

The four public methods are `synchronized`, so concurrent HTTP requests are safe.

Tests live in `WarmestApplicationTests`. `warmestComplexScenario` replays the
21-step example trace from the exercise and asserts every expected return value.

## Part 2 — REST API

Spring Boot exposes the data structure over HTTP. Four endpoints, one per interface
method.

| Method   | Path                              | Notes                                   |
|----------|-----------------------------------|-----------------------------------------|
| `POST`   | `/api/warmest/keys/{key}?value=N` | put. Returns prev value or null if new. |
| `GET`    | `/api/warmest/keys/{key}`         | get. 200 / 404.                         |
| `DELETE` | `/api/warmest/keys/{key}`         | remove. 200 with old value / 404.       |
| `GET`    | `/api/warmest/warmest`            | warmest key. 204 if empty.              |

Null or blank keys → 400, via `GlobalExceptionHandler`.

Smoke test:

```bash
curl -X POST   "localhost:8080/api/warmest/keys/a?value=100"
curl           "localhost:8080/api/warmest/keys/a"               # 100
curl           "localhost:8080/api/warmest/warmest"              # a
curl -X DELETE "localhost:8080/api/warmest/keys/a"               # 100
```

## Part 3 — three instances sharing state

Redis as the shared backend. `RedisWarmestDataStructureService` keeps the same
map + DLL design on the Redis side:

- `warmest:values` — `key -> value`
- `warmest:next`, `warmest:prev` — DLL pointers
- `warmest:head`, `warmest:tail` — ends of the list

Each put/get/remove is a single Lua script (`src/main/resources/redis/`) so the
value hash and the pointer hashes update atomically across racing instances.

A DLL stored in Redis hashes, because the alternatives break O(1): LIST +
`LREM` is O(N), ZSET + score is O(log N).

After `docker compose up`, all three containers share one Redis:

```bash
curl -X POST "localhost:8080/api/warmest/keys/hello?value=42"
curl         "localhost:8081/api/warmest/warmest"                # hello   (app2)
curl         "localhost:8082/api/warmest/keys/hello"             # 42      (app3)
```