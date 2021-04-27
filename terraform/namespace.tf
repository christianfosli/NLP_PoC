resource "kubernetes_namespace" "nlp" {
  metadata {
    name = "nlp"
  }
}
