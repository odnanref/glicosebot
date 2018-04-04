curl -X POST -H "Content-Type: application/json" -d '{
  "setting_type" : "domain_whitelisting",
    "whitelisted_domains" : ["https://$DOMAIN"],
	  "domain_action_type": "add"
  }' "https://graph.facebook.com/v2.6/me/thread_settings?access_token=$PAGE_ACCESS_TOKEN"
