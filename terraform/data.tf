variable "RESOURCE_GROUP" {
  type = string
}

variable "ENVIRONMENT" {
  type = string
}

variable "ENV" {
  type = string
}

variable "AKS_CLUSTER_NAME" {
  type = string
}

variable "AZ_KEYVAULT_NAME" {
  type = string
}

variable "SECRETS_SP_NAME" {
  type = string
}

# Cluster info
data "azurerm_subscription" "current" {
}

data "azurerm_kubernetes_cluster" "et" {
  name                = var.AKS_CLUSTER_NAME
  resource_group_name = var.RESOURCE_GROUP
}

# Key Vault where secrets and certificates are stored
data "azurerm_key_vault" "keyVault" {
  name                = var.AZ_KEYVAULT_NAME
  resource_group_name = var.RESOURCE_GROUP
}

# Service Principal for reading secrets
data "azuread_service_principal" "secretsServicePrincipal" {
  display_name = var.SECRETS_SP_NAME
}

data "azurerm_key_vault_secret" "secretsServicePrincipal" {
  name         = var.SECRETS_SP_NAME
  key_vault_id = data.azurerm_key_vault.keyVault.id
}

locals {
  common_tags = {
    Owner       = "Christian Fosli"
    Application = "ET"
    Department  = "DevOps"
    Environment = var.ENVIRONMENT
    Backup      = "False"
  }
}
