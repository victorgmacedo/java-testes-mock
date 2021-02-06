Feature: Propondo lances

Scenario: Propondo um unico lance valido
   Given Dado um lance valido
   When Quando propoe ao leilao
   Then Entao o lance eh aceito

Scenario: Propondo varios lances validos
   Given Dado um lance de "100" para o usuario "Victor"
     And Dado um lance de "200" para o usuario "Gabriel"
   When Quando propoe varios lances ao leilao
   Then os lances sao aceitos


Scenario Outline: Propondo um lance invalido
   Given Dado um lance de <valor> para o usuario "Victor"
   When Quando propoe varios lances ao leilao
   Then os lances nao sao aceitos

Examples:
   | valor |
   |     0 |
   |    -1 |
