# NLP_PoC
Proof of concept (POC) project: NLP

## Documentation in Confluence
Documentation is to be found in Confluence:
* NLP Microservice App: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1519910946/NLP+Microservice+App
* NLP Console App: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1484390404/NLP+Console+App
* NLP Service Api: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130856458/NLP+REST+API
* Transformer Service Api: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1519779841/RDF+transformation+API

## Run locally with docker-compose

docker-compose can be used to run the ServiceController, RDF Transformer, and NLP API
together on your local machine.

You will need a Personal Access Token with access to sdir's Nuget feed to build the ServiceController.
This can be generated in Azure DevOps.

You will also need to obtain a client secret for the nlpservicecontrollerapp client.
See the [documentation on confluence](https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1519910946/NLP+Microservice+App)
for how to use it.

Then run the following command in a console

```bash
COMPOSE_DOCKER_CLI_BUILD=1 DOCKER_BUILDKIT=1 docker-compose build \
  --build-arg ACCESS_TOKEN=<your personal access token from azure devops>
docker-compose up -d
```

The two variables at the start of that command can be omitted if you configure your docker
to build images with BuildKit by default.

ServiceController can now be reached on [http://localhost:5001](http://localhost:5001).
Use `docker-compose logs` or `docker-compose logs --follow` to see what's going on in all three services.
