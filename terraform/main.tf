terraform {
  backend "azurerm" {
    key = "terraform.tfstate"
  }

  required_providers {
    azuread = {
      version = "~>1.4"
    }
    azurerm = {
      version = "~>2.56"
    }
    helm = {
      version = "~>2.1"
    }
    kubernetes = {
      version = "~>2.1"
    }
  }
}

provider "azurerm" {
  features {}
}

provider "helm" {
  kubernetes {
    host     = data.azurerm_kubernetes_cluster.et.kube_config.0.host
    username = data.azurerm_kubernetes_cluster.et.kube_config.0.username
    password = data.azurerm_kubernetes_cluster.et.kube_config.0.password

    client_certificate     = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.client_certificate)
    client_key             = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.client_key)
    cluster_ca_certificate = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.cluster_ca_certificate)
  }
}

provider "kubernetes" {
  host     = data.azurerm_kubernetes_cluster.et.kube_config.0.host
  username = data.azurerm_kubernetes_cluster.et.kube_config.0.username
  password = data.azurerm_kubernetes_cluster.et.kube_config.0.password

  client_certificate     = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.client_certificate)
  client_key             = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.client_key)
  cluster_ca_certificate = base64decode(data.azurerm_kubernetes_cluster.et.kube_config.0.cluster_ca_certificate)
}
