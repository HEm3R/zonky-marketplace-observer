# Zonky Marketplace Observer

Observer of https://api.zonky.cz/loans/marketplace.

All loans are:
- printed to console
- saved to in-memory storage and can be read by simple API

## Running

`./gradlew bootRun`

## API for Latest Observation

```
http://localhost:8080/observation/latest
```

Query params:
- `limit`, default = `20`
- `offset`, default = `0`

## Docker (build & run)

```
./gradlew build
docker build -t zonky-marketplace-observer .
docker run -p 8080:8080 --rm zonky-marketplace-observer
docker run -p 8080:8080 --rm zonky-marketplace-observer
```
