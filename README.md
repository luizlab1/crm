
# Arquitetura

### Componentes
- **Webhook:** Serviço responsável por receber mensagens do WhatsApp via Twilio e persistir e encaminhar para o bot engine. Ele foi pensado para fazer apenas esta tarefa simples para reduzir riscos de falhas na entrada das mensagens no sistema.
- **Bot Engine:** Serviço principal que processa as mensagens recebidas, aplica a lógica de negócio, interage com o CRM e gera respostas.
- **CRM:** Sistema de gerenciamento de relacionamento com o cliente, utilizado para armazenar e gerenciar dados dos clientes e interações.

### Fluxo Principal
![Fluxo Principal](https://img.plantuml.biz/plantuml/svg/rLPDRziu4BqRy7yOU2yEag0VUoaAssWIMuM1fdMJ0js3WGKbJsoYCgabgJhvznsIaYNhomvGjDtu45kQuRnvV6_8TzemPT9j3FrUlwU_SP4mnRPmBHyNRBNYOWslNvPVB2YSHpnXmfG3x2UKZp1OYjcZGILO35DCKAnGH1prKWpWhvdxw_S0M6IaWfkOep30D4JkctsJ5El3uEk66NsL90CRixC_M9BKehxiUCobYp7kQxz7TO7Wb53DHIXJUleLmusKts2e2NgCe55zvn6UUO1lWbT1K2jmXzh0rMBkOVW5hX9kurRCi91fXC6j56ivlIx7J4CRyb-AARtF0Ip5P_l0nN7_p3s0qKWdYa1D9atuhwM1cLXpWJdIOFQc6saeJITAUdyK8SNCsq3k4p-bffufiuUdD7TpT-kw2eBWxzdBxguYjQrrjP3Qh1Ng-yyVyXZRMQdGU0otg3LROqwwLUvutEzvwS7bP2ymU0ixnGqEH-4a2yvoHo_2oLc_jm-a9AyuIdcGQiVKgZKf2xHrzWgobVoFrZIgLeGNoTEGSXDKcclJahiKGpAtIVLuCViudNzyVm69aX55KElZ1nPhFAkiFZbK95L8mvZhJJiJfUmo6QyVgwndzEIibn97bzMgLDAWEAde8I5fGffespVB1veo1qQbU04vINUVRcRtzyHJ0-lUIbJ1gAVWo4RtUomsUR-WqyYMWo5HScPLcotb5prX6mzo9ZL37j-Z8hsgFRTB0EryhZ36JZvbgY84-K2yf6O3N0DP6QRNZWOaXTdQW4hHj1yAAIwhB5GXPJ7Ti9wvpKxg-tqEhkOVPjEBfG25j9l_CZCMSdTEhRAd2s36u3On-loyQC7mBT8qWnUaq-neSntz6I1fo6LMo-TtRhbCg7u9qD46LscColalbZ81_8xquXyw0oCfLhgxdmSqqOPc92ewWGvW0gEYrLQMmZxDqOyiVBk-gqlU06IqchqVs8dWmkph2e3QF59uNEsEa7PM2g2EY4o8D8JgpZeoXmwSxENh5pyXmvzYb14f99a0CU85uqurEfNyUnzPdX8GRPXOezkiHWXXocCtd8Yh-Aa3SNNsTCLFAiVvmTHfIFGpi4JrtZ1r28LHQWlzJ9jO9vS0PNFvw4HkQEvW7Z-FbtPPBWfep_5h7VilycddaNJCfj3WKt-9B0YXJUIktrR3LgalxDef_VqbXNgvf_gD2P1hMDjBv5wz-IMkRBZIaVMxn9t9ss4j2Ri9ROQqT1ijRhqPvQsoBzzxislpMzZweEDUAMQUnVb5tgXj6_LE7wqZy8HIEVt51QQH_exhomd2_B_vjEgN0vSs4bgsw3lwdsxZVky7)

<details>
  <summary>Código do diagrama</summary>

Você poderá editar o código abaixo no site [https://editor.plantuml.com](https://editor.plantuml.com).

```plantuml
@startuml 

skinparam BoxPadding 20
skinparam ParticipantPadding 20


box "\nExternal Dependencies\n" #EEEEEE
  actor Client as client
  participant "WhatsApp" as whatsapp
  participant "Twilio" as twilio
end box

box "\nInbound Webhook (single service)\n" #E8F1FF
  participant "Rest API" as webhookApi
  database "MongoDB" as webhookDb
  queue "Inbound \nQueue" as inboundQueue <<spring>>
end box

box "\nBot Engine\n" #E8FFE8
  participant "Rest API" as botApi
  database "MongoDB" as botDb
  queue "Inbound \nQueue" as botInboundQueue <<spring>>
  queue "Outbound \nQueue" as outboundQueue <<spring>>
end box

box "\nCRM\n" #FFF3E0
  participant "Rest API" as crmApi
  database "PostgresSQL" as crmDb
end box

== Message inbounding ==
client -> whatsapp : write(<b>inboundMessage</b>)
whatsapp -> twilio : forward(<b>inboundMessage</b>)
twilio -> webhookApi : webhook(<b>inboundMessage</b>)

webhookApi -> webhookDb : persist(<b>inboundMessage</b>)\n(status=PENDING, processing=false)
webhookApi ->> inboundQueue : publish(<b>inboundMessage</b>)
webhookApi --> twilio : 2xx

== Message forwarding ==
webhookApi <- inboundQueue : listen(<b>inboundMessage</b>)
webhookApi -> webhookDb : update(<b>inboundMessage</b>)\n(set processing=true, status=PROCESSING)

webhookApi -> botApi : tryForward(<b>inboundMessage</b>)

alt forward success (2xx)
  botApi -> botDb : persist(<b>inboundMessage</b>)
  botApi ->> botInboundQueue : publish(<b>inboundMessage</b>)
  botApi --> webhookApi : 2xx
  webhookApi -> webhookDb : delete(<b>inboundMessage</b>)\n(source of truth is CRM DB)

else bot error (non-2xx)
  botApi --> webhookApi : non-2xx
  webhookApi -> webhookDb : update(<b>inboundMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)

else timeout / internal error
  webhookApi -> webhookDb : update(<b>inboundMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
end

== Inbound Message Forward retry scheduler ==
loop every X seconds
  webhookApi -> webhookDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    webhookApi ->> inboundQueue : republish(<b>inboundMessage</b>)
  end
end

== Inbound Message processing ==
botApi <- botInboundQueue : listen(<b>inboundMessage</b>)
botApi -> botApi : proces(<b>inboundMessage</b>)
botApi -> crmApi : create or update data
crmApi -> crmDb : persist changes
  
botApi -> botApi : build(<b>replyMessage</b>)
botApi -> botDb : persist(<b>replyMessage</b>)
botApi ->> outboundQueue : publish(<b>replyMessage</b>)

alt processing error
  botApi -> botDb : update(<b>replyMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
else processing success
  botApi -> botDb : persist(<b>replyMessage</b>)
  botApi ->> outboundQueue : publish(<b>replyMessage</b>)
end

== Inbound Message Processing retry scheduler ==
loop every X seconds
  botApi -> botDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    botApi ->> botInboundQueue : republish(<b>inboundMessage</b>) 
  end
end

== Outboud Message delivery processing ==
botApi <- outboundQueue : listen(<b>replyMessage</b>)
botApi -> twilio : trySend(<b>replyMessage</b>) 

alt send error
  twilio --> botApi : non-2xx
  botApi -> botDb : update(<b>replyMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)

else send success
  twilio --> botApi : 2xx
  twilio -> whatsapp : deliver(<b>replyMessage</b>)
  whatsapp -> client : message(<b>replyMessage</b>)
  botApi -> crmApi : sendToCrm(<b>replyMessage</b>) 
 
  crmApi -> crmDb : persist(<b>replyMessage</b>)
  botApi -> botDb : delete(<b>replyMessage</b>)\n(source of truth is CRM DB)
end

== Outboud Message delivery retry scheduler ==
loop every X seconds
  botApi -> botDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    botApi ->> outboundQueue : republish(<b>replyMessage</b>)
  end
end

@enduml
```
</details>
