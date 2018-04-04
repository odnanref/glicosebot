curl -X POST -H "Content-Type: application/json" -d '
{
  "persistent_menu":[
    {
      "locale":"default",
      "composer_input_disabled": false,
      "call_to_actions":[
        {
          "title": "Menu",
          "type":"nested",
          "call_to_actions":[
            {
              "title":"Ajuda",
              "type":"postback",
              "payload":"AJUDA"
            },
            {
              "title":"Relatório",
              "type":"postback",
              "payload":"REPORT"
            }
          ]
        }
      ]
    }
  ]
}  
' "https://graph.facebook.com/v2.6/me/messenger_profile?access_token="$PAGE_ACCESS_TOKEN
