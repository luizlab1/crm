
# Arquitetura

### Fluxo Principal
![Fluxo Principal](https://img.plantuml.biz/plantuml/svg/dLN9Rjim4BqBq3yCt4jo2DgYbw2eWoosWG0DcgG5Sic5aYOoOOfaIIgE_xuSabgyn4NhYxcyUJDl5khIEcPSsuWyoxD2lS7aMSxV71h91CnGewnGbXpjivp0XtduvHa0AvqoS2CuIWVCGXbMTACz7o-vPlvYyhHapbvfFI7CcZPCwptKhpKNN0MC2yiyytx1XpEAwbOMglMdJrWibLh5UCuNdnUBFRv7j0wkxc-ZruY_qfnm5NEiO1PXSgTahMRNOyoiEEJuMZcOovfBx7mkvkV7VHRA7VNdxyaN0NwtsFhRJjqTMijgX0SwVfOdLXikQy1NdzsF0NiQ67Z41zY8vaVh_er79OD4jA_-vl4ko5ui5b_cduxBBasp9_jUMLSRj3yVldUOdJHFfz2fdqxpB7OId5tq_G9VeOboygm_y_UnL_pjYp9hPgeyIoTasrVTtwSDEHkT3wXPuK4QZULUJChioEKXaqXiooLMhK2eKF1ND1imwCmcgKgT2au1zH294qhfY07sugUC3Dc6ChRj9JQGzv28XsZyw0at-4DZbA7JNOP3qW8OXAh908KTnKtPEKxICBFgmg129bEi0fciGasJSfBBfMCh3AgJcgZhUB9xwCLMYvD9MmXkbx651-1kILYvacejiAe7TIcgcMeObw2DAhqx6eSJBYslCCJhc_Xq7Estizq8IYLjs-21O8Tw9NAeeY-VQrxccT8E_221yhgPuvw7aZQOny4X5mQPG_1LQdL5AuEbCbM09r24RsMnN39Pe-qhG1qoKjdtoFjr67L6w8kHjMrBMXwt7-7zhBV2RS4ljf-PKJ4DQh4PTqx_WFMT-PSQxJ9tHKhCxuVS6HugP_-oM7gd4XLbDUp_8v_1xc0c-zAdWdSJvh_BhdqdwmORzOh310uIKY8l_N_ufly1)
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

box "\nInbound Webhook\n" #E8F1FF
  participant "Rest API" as webhookApi
  database "MongoDB" as webhookDb
end box

box "\nBot Engine\n" #E8FFE8
  participant "Rest API" as botApi
  database "MongoDB" as botDb

  queue "Inbound Message Queue\n(spring event/queue)" as inboundQueue
  queue "Outbound Message Queue\n(spring event/queue)" as outboundQueue
end box

box "\nCRM\n" #FFF3E0
  participant "Rest API" as crmApi
  database "PostgresSQL" as crmDb
end box

== Inbound ==
client -> whatsapp : message
whatsapp -> twilio : forward
twilio -> webhookApi : webhook

webhookApi -> webhookDb : persist inbound message
webhookApi -> webhookApi : schedule delivery retry

== Webhook to Bot ==
loop retry after delay
  webhookApi -> botApi : deliver message
  alt delivery error
    webhookApi -> webhookDb : persist error log
  else delivery ok
    webhookApi -> webhookDb : mark message as delivered
  end
end

== Bot intake ==
botApi -> botDb : persist inbound message
botApi ->> inboundQueue : publish event
botApi --> webhookApi : acknowledge delivery

== Domain processing (inside Bot API) ==
botApi <- inboundQueue : consume message
botApi -> botApi : process domain logic
opt CRM operations
  botApi -> crmApi : create or update records
  crmApi -> crmDb : persist changes
end

alt processing error
  botApi -> botDb : persist error log
else processing success
  botApi -> botDb : persist processing result
  botApi ->> outboundQueue : publish reply
end

== Outbound delivery (inside Bot API) ==
botApi <- outboundQueue : consume reply
botApi -> botDb : persist outbound message
botApi -> twilio : send reply
alt send error
  botApi -> botDb : persist error log
else send success
  botApi -> crmApi : update message status
  botApi -> botDb : remove delivered message
end

@enduml
```
</details>
