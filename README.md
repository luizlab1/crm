# WhatsApp CRM Bot Platform

## Stack

- Kotlin
- Spring Boot
- MongoDB
- PostgreSQL
- Kafka
- Twilio API
- WhatsApp Business API

## Arquitetura

O sistema segue **Arquitetura Hexagonal com DDD (Domain-Driven Design)**, com separação clara entre:

- domínio de negócio
- casos de uso
- infraestrutura e integrações externas

Essa abordagem reduz acoplamento, melhora testabilidade e facilita evolução do sistema.

## Componentes

### Webhook

Responsável por:

- receber mensagens do WhatsApp via **Twilio**
- persistir a mensagem recebida
- publicar o evento para processamento

Foi projetado para executar **uma responsabilidade simples e crítica**, reduzindo o risco de perda de mensagens na entrada do sistema.

### Bot Engine

Serviço principal que:

- consome mensagens da fila
- processa a lógica do bot
- interage com o CRM
- gera respostas para o usuário
- executa retry e recuperação de fluxo

### CRM

Responsável por:

- persistência definitiva das interações
- gerenciamento de clientes
- histórico de conversas
- atualização de dados de negócio

O **CRM é a source of truth** dos dados finais.

## Fluxo Principal

![Fluxo Principal](https://img.plantuml.biz/plantuml/svg/fLPHZzeu47v7uZ_CSIyWNNVtUgfKhHRKPKdka9OjjxlNxGDIvIG3M0GxPpiBfwh_lMCxWP10YtJmWEryC_Dvvfi97xHXokJhjDtIAouofjWQXd8xPKd2nGBUNbKVJ0dBOvunOQg0TYkIM-ZCHB0rg0HBOOGPYWH5p57FH0T-3TodtG9WiP4AxbAEmW3J4BkLVPBjlKFdPScClisoZiLix8PbMGFrlE4fbmvZtBBTehX0T2ginYAIEPs-OBIKSWLTJSHJ18tgbSVOymJVXx-7OIF08se3jzEnf-4Tt6OSRcvMqgHS30RM9666HKmZT4R5gegtyRUZY6mcKYoaDMcI35vjFePAnYjKZb5uPR_M_RyvvkIxlCTCFCkCnou4zsVkm99YynKx7c0e3GHYmGMMzms3zp--RoJDsNSbx5dtl7kS-Dk5uH_1rO_ZnsfzTdp2kj1JgSr2eNxw-xv6eD-72MhD5bXcpUgtts-tl0JXpM0dDBY6ZU86kpVHW8k9NjnSHeDUkxLxJbWlE4BEfTembTInnNFhTQ-Rwt9JHvFlFMflK-Rq6Z9KccjpQ3SJNMfWa-l-D7WOZH_-_19y2XwUUpLlWqETaBCyIhbUDfmPlaQPc_uxNNVH99HdL0eynSVn-3BzPtW_Vleh6DNSLTFtTbkMfspGHupbAwh_OEffeeAdU8b9djBA5YoLCWDqa7VKnh4KYRRQ-0dZEPfuIJL6HSr_96tMXNDD5GLaX1NKD4MkC055aLHYKRLGv-NtYE7_poe0ITpfl4YYxt6OqYGXjPSIBrlvJPqfqJGewaGBE16izFK93TguCZDbH9WmGJscG1AQ6Iw5Ays1Hzy9cJSey1V5zbOamvodoSVSK8Hc1lUEYl7GS4JdTqic5gYx5nSNpgY0UgEJL_aDQ2TTNB-T2JhmLTVUPNA4nqx9DHwQYk9VKDq3lywyCXtsCoV1b9EgZy2hWwSOLzUWbtBJm1voEnWzDNuK3Gs-b0gqsHu5zy5p09U5sUNNCZrZT_5llJEwpBjjQgIcWRg18q35B-fkAsTrBtCL8yWv69MR9N0Dz0Q1qR1NrVnkO5pIKkThQkzJy-9pTLwS8EJdn3oL6pyiPiAtto_zSFpW6WyOlLUoNOlrvopLs1mVof9X4VtNg8wEdxU2iyDGoVKzq-R9O6RJMO36UmrMM_y3_Rh_STy1)

<details>
  <summary>Código do diagrama</summary>

Você pode editar este código em https://editor.plantuml.com

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
  participant "Rest API" as webhookApi <<kotlin>>
  database "Database" as webhookInboxDb <<Mongo>>
end box

box "\nEvent Broker\n" #F0F0F0
  queue "Inbound \nTopic" as inboundTopic <<kafka>>
end box

box "\nBot Engine\n" #E8FFE8
  participant "Rest API" as botApi <<kotlin>>
  database "Database" as botInboxDb <<Mongo>>
end box

box "\nCRM\n" #FFF3E0
  participant "Rest API" as crmApi <<kotlin>>
  database "Database" as crmDb <<PostgreSQL>>
end box

== Message Inbound ==
client -> whatsapp : write(<b>inMsg</b>)
whatsapp -> twilio : forward(<b>inMsg</b>)
twilio -> webhookApi : webhook(<b>inMsg</b>)

webhookApi -> webhookInboxDb : persist(<b>inMsg</b>)\n(status=PENDING_EVENT)
webhookApi ->> inboundTopic : publish(<b>inMsg</b>)\n(timeout=5s)
webhookApi -> webhookInboxDb : update(<b>inMsg</b>)\n(status=EVENT_PUBLISHED)
webhookApi --> twilio : 2xx

== Webhook Recovery Publisher Scheduler ==
loop every 5 seconds
  webhookApi -> webhookInboxDb : fetch where status=PENDING_EVENT
  loop for each not published record
    webhookApi ->> inboundTopic : publish(<b>inMsg</b>)
    webhookApi -> webhookInboxDb : update(<b>inMsg</b>)\n(status=EVENT_PUBLISHED)
  end
end

== Engine Processing ==
botApi <- inboundTopic : listen(<b>inMsg</b>)
botApi -> botInboxDb : persist(<b>inMsg</b>)

opt Process Manager operations
  botApi -> botApi : processManager.processAndBuildOutMsg(<b>inMsg</b>)
  botApi -> crmApi : processManager.crmChanges(...)
  crmApi -> crmDb : persist(<b>inMsg</b>, <b>outMsg</b>, \n<b>recordsChanged</b>)
  botApi -> twilio : processManager.sendTwilio(<b>outMsg</b>)
  twilio -> whatsapp : deliver(<b>outMsg</b>)
  whatsapp -> client : show(<b>outMsg</b>)

  botApi -> webhookApi : processManager.deleteWebhookInMsg(DELETE /webhook/{<b>inMsgId</b>})
  webhookApi -> webhookInboxDb : delete(<b>inMsg</b>)

  botApi -> botInboxDb : deleteInMsgAndOutMsg(...)\n(source of truth is CRM DB)
end

== Recovery / Resume Scheduler ==
loop every X seconds
  botApi -> botInboxDb : fetch flow where\nstatus!=FINISHED and processing=false
  loop for each resumable record
    botApi -> botApi : processManager.resumeFromLastStep(<b>processId</b>)
  end
end
@enduml
```
</details>

## Fluxo de Processamento do Bot

![Process Flow](https://img.plantuml.biz/plantuml/svg/ZLHBZzem4Bv7od-OxebANQXwN16qK88Q2HO4IDl3IiX99c1XxDIFibMr_xss4rW2bFO2ZkTxp8mzVbIQI6rsB0pKblAIIBA3WNYTaJodV0rVFfy6PXPBCreIhay1OL2-1-1kkQUCYZiW2hHVdiKJdWh3StZ6T2F45lgpn8FtrNU_fD3jReLcbFTwOV33eC5trbAKDFDuMktu3SSWnPOuGiDi8FJH81NwN3mdcgH4OGLq7gym71pG_dkOMiP4yBMu4Fywdnp5Cxax5m-3A8AOhob7c4cHeLAkPL4K1eTKxfjLF02ZIYDlTTCUvHErxdRItiSZ_BvtcjK3b2YLnP-Zmq2K-k084yB96cKO03HatCfALAWH4_isvHfbAV52y9HPrzbasis4Kqq9czgSYAQ2grQxtVR9_DCtDPJbKwEjuItdlvKkFFDO-ocbkdzdTWfjkttWWWr0c8Og2CfaZkDs_wS18yglbD_adbpn9Zz7XXhhYKXS0rh3U1mlO-ZKdCxlGskIt6NxTY7Uw4ybw9Nw_D1Sbz8BRobXP8OW2j3Iw0rG1VOcUpraTXRg-b5A8M-QcDBE3pRFpEeNjgALVLYqKT4YVXgkbjFLyZaP9zDLF9zFvvzUk8UKnvc82cADgmnu7WR-nu_F73Fn4-KlwDYbCZi4bMqmDmob7oGcH0de4Tz1MJ3FrOrhKw3EDh3VeCGwYIhF3z4eUKeMt-8X4Fl8N4tFoNgxGaX0OdMazPFvbHPL1onzmYCfTcEYz49ZwLfLorQdU57oe_stp_4V)

<details>
  <summary>Código do diagrama</summary>

Você pode editar este código em https://editor.plantuml.com

```plantuml
@startuml
skinparam BoxPadding 20
skinparam ParticipantPadding 20

participant "Twilio" as twilio
participant "Inbound Webhook API" as webhookApi <<kotlin>>
queue "Inbound Topic" as inboundTopic <<kafka>>
participant "Bot API" as botApi <<kotlin>>
database "Bot Inbox DB" as botInboxDb <<Mongo>>
participant "CRM API" as crmApi <<kotlin>>

== Engine Processing ==
botApi <- inboundTopic : listen(<b>inMsg</b>)
botApi -> botInboxDb : persist(<b>inMsg</b>)

opt Process Manager
  botApi -> botApi : processFlow.interpret(<b>inMsg</b>)
  botApi -> crmApi : processFlow.crmInitialOperations(...)

  botApi -> botApi : processFlow.buildOutMsg(<b>inMsg</b>)
  botApi -> botInboxDb : persist(<b>outMsg</b>)

  botApi -> twilio : processFlow.sendTwilio(<b>outMsg</b>)
  alt Twilio success
    botApi -> crmApi : processFlow.crmFinalOperations(...)
    botApi -> webhookApi : processFlow.deleteWebhookInMsg(DELETE /webhook/{<b>inMsgId</b>})
    botApi -> botInboxDb : deleteInMsgAndOutMsg(...)\n(source of truth is CRM)
  else Twilio error
    botApi -> botInboxDb : update(<b>inMsg</b>)\n(flow_status=SEND_TO_TWILIO_ERROR,\nflow_processing=false)
  end
end

== Recovery / Resume Scheduler ==
loop every X seconds
  botApi -> botInboxDb : fetch where\nflow_status!=FINISHED and flow_processing=false
  loop for each record
    botApi -> botApi : resumeFromLastStep(<b>processId</b>)
  end
end
@enduml
```
</details>

## Legenda

- **inMsg**: mensagem de entrada
- **outMsg**: mensagem de saída
- **PENDING**: registrada e aguardando processamento
- **PROCESSING**: em processamento ativo
- **FAILED**: falha ocorrida; elegível para retry
- **processing=false**: mensagem livre para reprocessamento
- **retry_at**: instante mínimo para nova tentativa
- **attempts**: contador de tentativas realizadas
- **publish / republish**: envio inicial / reenvio para fila
- **2xx / non-2xx**: resposta de sucesso / erro HTTP
- **source of truth**: banco responsável pelo dado final

## Estrutura do Projeto

```text
sales
├── domain
│   ├── model
│   ├── repository
│   ├── service
│   ├── event
│   ├── exception
│   ├── specification
│   └── valueobject
│
├── application
│   ├── usecase
│   ├── port
│   │   ├── input
│   │   └── output
│   ├── command
│   ├── query
│   ├── dto
│   ├── mapper
│   └── service
│
└── infrastructure
    ├── web
    │   ├── controller
    │   ├── request
    │   ├── response
    │   └── mapper
    ├── persistence
    │   ├── entity
    │   ├── repository
    │   ├── mapper
    │   └── specification
    ├── messaging
    │   ├── producer
    │   ├── consumer
    │   └── mapper
    ├── client
    │   ├── http
    │   ├── grpc
    │   └── mapper
    ├── scheduler
    └── config
```

## Como rodar e acessar

Pré-requisitos

- Java 25
- Docker & Docker Compose
- (Opcional) Gradle wrapper já incluído

Passos mínimos

1) Subir o banco (no diretório `infra-crm`):

```powershell
cd infra-crm
docker compose up -d
```

2) Rodar a aplicação (na raiz do projeto):

```powershell
cd ..
.\gradlew.bat bootRun
```

URLs úteis

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: GET http://localhost:8080/health/live  → {"status":"UP"}

Pronto — apenas isso é necessário para subir a aplicação e acessar a documentação.
