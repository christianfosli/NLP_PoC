Feature: NLP namespace
  In order to deploy NLP related resources
  As cloud engineers
  We require a kubernetes namespace


  Scenario: NLP namepsace
    Given I have kubernetes_namespace defined
    When its address is kubernetes_namespace.nlp
    Then it must contain metadata
    And it must contain name
    And its value must be "nlp"
