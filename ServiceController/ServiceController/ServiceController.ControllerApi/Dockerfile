FROM mcr.microsoft.com/dotnet/aspnet:3.1-buster-slim AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:3.1-buster AS build
ARG BUILD_CONFIG=Release
# This block authenticates Docker with the private Sdir NuGet repo.
# Pass "ACCESS_TOKEN" using --build-arg in docker build step
ARG ACCESS_TOKEN
ENV DOTNET_SYSTEM_NET_HTTP_USESOCKETSHTTPHANDLER 0
ENV NUGET_CREDENTIALPROVIDER_SESSIONTOKENCACHE_ENABLED true
ENV VSS_NUGET_EXTERNAL_FEED_ENDPOINTS {\"endpointCredentials\": [{\"endpoint\":\"https://pkgs.dev.azure.com/sdir-no/Core/_packaging/Libraries/nuget/v3/index.json\", \"username\":\"ArtifactsDocker\", \"password\":\"${ACCESS_TOKEN}\"}]}
RUN wget -qO- https://raw.githubusercontent.com/Microsoft/artifacts-credprovider/master/helpers/installcredprovider.sh | bash

WORKDIR /src
COPY ["ServiceController.ControllerApi/ServiceController.ControllerApi.csproj", "ServiceController.ControllerApi/"]
COPY ["ServiceController.ControllerApi/NuGet.Config", "ServiceController.ControllerApi/"]
COPY ["ServiceController.AuthenticationService/ServiceController.AuthenticationService.csproj", "ServiceController.AuthenticationService/"]
COPY ["ServiceController.Entities/ServiceController.Entities.csproj", "ServiceController.Entities/"]
COPY ["ServiceController.KnowledgeService/ServiceController.KnowledgeService.csproj", "ServiceController.KnowledgeService/"]
COPY ["ServiceController.NlpService/ServiceController.NlpService.csproj", "ServiceController.NlpService/"]
COPY ["ServiceController.TextService/ServiceController.TextService.csproj", "ServiceController.TextService/"]
COPY ["ServiceController.TransformerService/ServiceController.TransformerService.csproj", "ServiceController.TransformerService/"]
RUN dotnet restore ServiceController.ControllerApi/ServiceController.ControllerApi.csproj
COPY . .
WORKDIR "/src/ServiceController.ControllerApi"
RUN dotnet build ServiceController.ControllerApi.csproj -c ${BUILD_CONFIG} -o /app/build

FROM build AS publish
ARG BUILD_CONFIG=Release
RUN dotnet publish ServiceController.ControllerApi.csproj -c ${BUILD_CONFIG} -o /app/publish

FROM build AS ci-files
ENV PATH=$PATH:/root/.dotnet/tools
RUN dotnet tool install --global Swashbuckle.AspNetCore.Cli
#TODO: generate open api docs when service updated to include them
#RUN swagger tofile --output /tmp/swagger_internal.json /app/build/ServiceController.ControllerApi.dll internal
#RUN swagger tofile --output /tmp/swagger_external.json /app/build/ServiceController.ControllerApi.dll external
#RUN swagger tofile --output /tmp/swagger_open.json /app/build/ServiceController.ControllerApi.dll open

FROM base AS final
COPY --from=publish /app/publish .
COPY --from=ci-files /tmp/swagger_*.json /tmp/
ENTRYPOINT ["dotnet", "ServiceController.ControllerApi.dll"]
