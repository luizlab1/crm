
# Arquitetura

### Componentes
- **Webhook:** Serviço responsável por receber mensagens do WhatsApp via Twilio e persistir e encaminhar para o bot engine. Ele foi pensado para fazer apenas esta tarefa simples para reduzir riscos de falhas na entrada das mensagens no sistema.
- **Bot Engine:** Serviço principal que processa as mensagens recebidas, aplica a lógica de negócio, interage com o CRM e gera respostas.
- **CRM:** Sistema de gerenciamento de relacionamento com o cliente, utilizado para armazenar e gerenciar dados dos clientes e interações.

### Fluxo Principal
![Fluxo Principal](https://img.plantuml.biz/plantuml/svg/pLTTJnix47rVQV_3A7q90jJUUr-gAeaA95j5AfH2fVOXKkNT38brD_RMzXRuzntR-xt9GX_wmGl1S-mvS-QCJTvgmvJ9Toc4GHZ4yW56Ax5uCAW4Is6E6OerYeIZNeaHl5Yudp00O8cH2YvIZi80qv2uJpQIqO4yuHcZmEZpbXbzbcKZYxctVx0iww4-tVEKIuSnxcCOK5uWEWrMIn7Bd5O_OxoLydyOQousAO96zPqdUEG9lex-YQ9UWXlK1iwkbvw6F-0iunQtPeR5J2ECBgNOoFbv4pEFBUHRZZd5Im8hSObQimqw9FUh7nrcCj6P8bgpsRuApgM1XTXmWINNQF5wc6iipI1FYZ-38w6MXsXMMp_aPjzUMIm_fy2BcqjNMHH5_orU3LUMg5slickfpKQXlltuliJOwXg9fbCeo9N24ncOJiF06n1EPvNDu0tiF3OCgZMAUujHz4wgUwRMOL2isBsLDoXU_67pDjPhr3mcK8PASst6atXMC2e8Jbx6CpBbc2RCv7fwlRYQBw_UdK2cP488uZszOwd6eyxfixQbA4CUfrnl3sHexswMz-z3ALda2mM5HZs2JhQupbDKJhTKoWpmErBW2yrI8iLQswuShlWEJR8bbL6HZulgehFb-yKS67MgMzzAs5MN-YipawcGz_PybvSw0CZeD7lFs4uwc0rt2FH4KVYKB01qfZDFnpX5sqlZDF9CJhjfA8D1yIpfsoBb6TaQ1ro1fgcKKJcUG6cLcmyNYzjRSijHttl-HgZTUv1R6B3Kb2ZGUM9pmPXSuSGfpl4dFiF9hJsptfNoj4thxTqvSqOTSjmQKnmKK-OgGP1tG3gQBN0DT1N1_Dp9W6H2cnjGAVBRM4XnsbMXIwd03DFwoHwxQQYQx2VcP2MgqR2JSKobiiSJOCRWBZFw-BWkmV0TqcqCBycanGFjAlexIDPpvfyukAuETMDMA4qZrhVE4rFMighlJy4OrdB7kA3BPCCJcrvcnhLUafcPuLBejjVzgqC790f95Y0_U87SAsIn1S9ZctC1oPQ93UggL3jUjN9rB_gJLVU0hgtqyLTrm9gaGQWO-IU7lSUfC-hj_m1QWxvdQ-s36nlygJUdJ_CtlJK3rROUchrI7NXc7DPUNTmLsfw1RfPw0lQag6MftdPD-GwWlJ4jeBPa-GzESuGQLzoVScpXMSUruTPzR7jCgqaUb8A-g90OX6sMMm_v0Jt-d2Ba9gk89Jn8jXViNLS7xRglkFg1tLlSmFFQjlzR-arV9iFW1m00)

<details>
  <summary>Código do diagrama</summary>

Você poderá editar o código abaixo no site [https://editor.plantuml.com](https://editor.plantuml.com).

```plantuml
@startuml 

box "\nExternal Dependencies\n" #EEEEEE
  actor Client as client
  participant "WhatsApp" as whatsapp
  participant "Twilio" as twilio
end box

box "\nInbound Webhook (single service)\n" #E8F1FF
  participant "Rest API" as webhookApi
  database "MongoDB" as webhookDb
  queue "Inbound \nMessage" as inboundQueue <<spring>>
end box

box "\nBot Engine\n" #E8FFE8
  participant "Rest API" as botApi
  database "MongoDB" as botDb
  queue "Inbound \nMessage" as botInboundQueue <<spring>>
  queue "Outbound \nMessage" as outboundQueue <<spring>>
end box

box "\nCRM\n" #FFF3E0
  participant "Rest API" as crmApi
  database "PostgresSQL" as crmDb
end box

== Message inbounding ==
client -> whatsapp : message
whatsapp -> twilio : forward
twilio -> webhookApi : webhook

webhookApi -> webhookDb : persist(<b>inboundMessage</b>)\n(status=PENDING, processing=false)
webhookApi ->> inboundQueue : publish(<b>inboundMessage</b>)
webhookApi --> twilio : 2xx

== Forward retry scheduler ==
loop every X seconds
  webhookApi -> webhookDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    webhookApi ->> inboundQueue : republish(<b>inboundMessage</b>)
  end
end

== Message forwarding ==
webhookApi <- inboundQueue : listen(<b>inboundMessage</b>)
webhookApi -> webhookDb : update(<b>inboundMessage</b>)\n(set processing=true, status=PROCESSING)

webhookApi -> botApi : forward(<b>inboundMessage</b>)

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

== Engine Processing ==
botApi <- botInboundQueue : listen(<b>inboundMessage</b>)
botApi -> botApi : process domain logic

opt CRM operations
  botApi -> crmApi : create or update data
  crmApi -> crmDb : persist changes
end

alt processing error
  botApi -> botDb : update(<b>replyMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
else processing success
  botApi -> botDb : persist(<b>replyMessage</b>)
  botApi ->> outboundQueue : publish(<b>replyMessage</b>)
end

== Reply retry scheduler ==
loop every X seconds
  botApi -> botDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    botApi ->> outboundQueue : republish(<b>replyMessage</b>)
  end
end

== Message reply delivery ==
botApi <- outboundQueue : listen(<b>replyMessage</b>)
botApi -> twilio : send(<b>replyMessage</b>)

alt send error
  twilio --> botApi : non-2xx
  botApi -> botDb : update(<b>replyMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)

else send success
  twilio --> botApi : 2xx
  botApi -> crmApi : send(<b>replyMessage</b>)

  alt crm non-2xx
    crmApi --> botApi : non-2xx
    botApi -> botDb : update(<b>replyMessage</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
  else crm 2xx
    crmApi --> botApi : 2xx
    crmApi -> crmDb : persist(<b>replyMessage</b>)
    botApi -> botDb : delete(<b>replyMessage</b>)\n(source of truth is CRM DB)
  end
end

@enduml
```
</details>
