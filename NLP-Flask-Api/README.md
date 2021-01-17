# NLP Flask API

This API is documented at the Confluence page: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130856458/NLP+REST+API

## Prerequisites

Docker and docker-compose (included in Docker Desktop).

## Running locally

Start the development server with:

```console
docker-compose up -d --build
```
Now you can reach it on localhost:5000.
http://localhost:5000/healthz should return "OK".

To check what is going on in the server do

```console
docker-compose logs
```

To stop the development server do

```console
docker-compose down
```
