
# Arquitetura

### Componentes
- **Webhook:** Serviço responsável por receber mensagens do WhatsApp via Twilio e persistir e encaminhar para o bot engine. Ele foi pensado para fazer apenas esta tarefa simples para reduzir riscos de falhas na entrada das mensagens no sistema.
- **Bot Engine:** Serviço principal que processa as mensagens recebidas, aplica a lógica de negócio, interage com o CRM e gera respostas.
- **CRM:** Sistema de gerenciamento de relacionamento com o cliente, utilizado para armazenar e gerenciar dados dos clientes e interações.

### Fluxo Principal
![Fluxo Principal](https://img.plantuml.biz/plantuml/svg/rLTTRnCx47sFbFym2Y-fsWekzmMX9A9jagj854ABn7s8XBoRQMAniVVQNbB-_HtRkrdl9qL28Dg79lRn-9pncR7xHXkcJ7P8O3WO3lGNBbAcs06kvUEQRRTSxE3bYt1YJN0UyvG94mBiRoGVORGHYqU3Ih04vfYYsAA8EUgD6C6pXViP3W1ORAI2cuIZCC0qnEwJdKdB-33wj6T6NwNfo6AEzWjBqmRguv4dN3gCSH-70zeNY4x0QYKYcT7e9upsKdw1iIRg2O96zPN7UEO9lbh-jLmsDhX3RU1glV8qV82hb5lSbXaMCOqmkfLY9-VN8MOUMSX_6MOqNn3OY0zsmE6u7tC3C9deL16fsQoD_xKqi10xBh1WkboywcSQITFBakQ_ov0mgowIfuNlCzDSAVF1fuYxkRjrgfRBvT-B5_sgOdLeg5fBRNOAzVs7jmN6AWiscaxX5hLcEooIRfqxdGu7tdfmEJiP35x3KN63uqaqu-9MxoRFezdPS72Q9wmt6Y4Vf3eojQrZytaRzUGN6zT_gQCfTeagrymZMfAYqboRwfAD65FXcanFrujtyzMxVouWLJ8cXIHh-i0IZMUreBEgsoXm5YLSxnigWqMXq9UFzKJcqej41Widb_NDQ2U3ec-lgkeifTF5fcWqeL2ZCho08XDtxsyMz_UKZ9QC-bgWm4PzMxOVsN304bE80ft5TXSOa-upwxOyWe_LVJ8LwAnHFvrfBrVLFUCI3z2Tgoqcs9OhcQaOGJwGvipiWMkWEeFvjTEAP16x9Q1Ir8h7GehBkjGwanpJp-Pf9-SiUZgwvTNgxM9-iH6Wa0xeCpDJ8O_d98nzkm1c31vIey_FI-Q67v0w33mde-NNZHFoMxbHlybBf6YbHQdablCXGCTxt6O9AbStYPGfu5UaYN_fEegbsEf-6GzeuZsrArHq6LHe0gDDwr8smeuMx2UMletlzYMx0pAAPgzgMyaEPgTh11hjGs6darqUkfDHydFwSxTHpsWMItlRg9IYjwQFsGtqjmO1OuLa0I3PtWpk5g5RmCzxP5ZT4E-PsA4xaTQTeumdhfUG7Jierhj5Et9Mkp8hhQAon7Ug88qdwpUtBIqVXFYPdhVL6539--PtEsP5KALbFZKBtGPRbtH-fE2QV7zHeVNU5nrr1Ys5vjzXPHxelBYJsLbnzLoNzTRXvjDpWCZVqyurA1JcrFRjTR9byHeAwoQuJ7wzQtFVEfQ1OzjuvXo31rtvJCmJtEBdyAsOlp1Vmy6VIv_zoqPbkNsKD-hGJB4x-ywErHk-I73vPgWbkEVHy0IZ_MdbLlT_fTXgIGoBxGtzfN-Q_mS0)

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
client -> whatsapp : write(<b>inMsg</b>)
whatsapp -> twilio : forward(<b>inMsg</b>)
twilio -> webhookApi : webhook(<b>inMsg</b>)

webhookApi -> webhookDb : persist(<b>inMsg</b>)\n(status=PENDING, processing=false)
webhookApi ->> inboundQueue : publish(<b>inMsg</b>)
webhookApi --> twilio : 2xx

== Message forwarding ==
webhookApi <- inboundQueue : listen(<b>inMsg</b>)
webhookApi -> webhookDb : update(<b>inMsg</b>)\n(set processing=true, status=PROCESSING)

webhookApi -> botApi : tryForward(<b>inMsg</b>)

alt forward success (2xx)
  botApi -> botDb : persist(<b>inMsg</b>)
  botApi ->> botInboundQueue : publish(<b>inMsg</b>)
  botApi --> webhookApi : 2xx
  webhookApi -> webhookDb : delete(<b>inMsg</b>)\n(source of truth is CRM DB)

else bot error (non-2xx)
  botApi --> webhookApi : non-2xx
  webhookApi -> webhookDb : update(<b>inMsg</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)

else timeout / internal error
  webhookApi -> webhookDb : update(<b>inMsg</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
end

== Inbound Message Forward retry scheduler ==
loop every X seconds
  webhookApi -> webhookDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    webhookApi ->> inboundQueue : republish(<b>inMsg</b>)
  end
end

== Inbound Message processing ==
botApi <- botInboundQueue : listen(<b>inMsg</b>)
botApi -> botApi : proces(<b>inMsg</b>)
botApi -> crmApi : create or update data
crmApi -> crmDb : persist changes
  
botApi -> botApi : build(<b>outMsg</b>)
botApi -> botDb : persist(<b>outMsg</b>)
botApi ->> outboundQueue : publish(<b>outMsg</b>)

alt processing error
  botApi -> botDb : update(<b>outMsg</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)
else processing success
  botApi -> botDb : persist(<b>outMsg</b>)
  botApi ->> outboundQueue : publish(<b>outMsg</b>)
end

== Inbound Message Processing retry scheduler ==
loop every X seconds
  botApi -> botDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    botApi ->> botInboundQueue : republish(<b>inMsg</b>) 
  end
end

== Outboud Message delivery processing ==
botApi <- outboundQueue : listen(<b>outMsg</b>)
botApi -> twilio : trySend(<b>outMsg</b>) 

alt send error
  twilio --> botApi : non-2xx
  botApi -> botDb : update(<b>outMsg</b>)\n(set processing=false, status=FAILED,\n retry_at=now+delay, attempts++)

else send success
  twilio --> botApi : 2xx
  twilio -> whatsapp : deliver(<b>outMsg</b>)
  whatsapp -> client : message(<b>outMsg</b>)
  botApi -> crmApi : sendToCrm(<b>outMsg</b>) 
 
  crmApi -> crmDb : persist(<b>outMsg</b>)
  botApi -> botDb : delete(<b>outMsg</b>)\n(source of truth is CRM DB)
end

== Outboud Message delivery retry scheduler ==
loop every X seconds
  botApi -> botDb : fetch where\nstatus=FAILED and processing=false\nand retry_at<=now
  loop for each pending retry
    botApi ->> outboundQueue : republish(<b>outMsg</b>)
  end
end

@enduml
```
</details>

### Legenda do Diagrama

- **inMsg** — mensagem de entrada (*inbound message*).
- **outMsg** — mensagem de saída (*outbound message*).
- **PENDING** — registrada e aguardando processamento.
- **PROCESSING** — em processamento ativo.
- **FAILED** — falha ocorrida; elegível para retry.
- **processing=false** — mensagem livre para reprocessamento.
- **retry_at** — instante mínimo para nova tentativa.
- **attempts** — contador de tentativas realizadas.
- **publish / republish** — envio inicial / reenvio para fila.
- **2xx / non-2xx** — resposta de sucesso / erro HTTP.
- **source of truth** — banco responsável pelo dado final.

