(config
    (text-field
        :name         "username"
        :label        "Username"
        :placeholder  "Enter Username"
        :required     true
    )

    (password-field
        :name         "password"
        :label        "apiKey"
        :placeholder  "Enter your Password"
        :required     true
    )

)

(default-source (http/get :base-url "https://marketingpony.us1app.churnzero.net/public/v1"
                    (paging/url-key :url_value_path_in_response "next")

                    (Auth/http-basic )
                    (header-params 
                                   "Username" "{username}",
                                   "Password" "{password}"
                                   "Accept"  "application/json")
                    (error-handler
                        (when :status 400 :message "Bad Request" :action skip )
                        (when :status 401 :message "Unauthorized"  :action refresh)
                        (when :status 403 :message "Forbidden")
                        (when :status 404 :message "Not Found"  :action fail)
                        (when :status 409 :message "Conflict")
                        (when :status 429 :message "Too Many Requests"  :action rate-limit)
                        (when :status 500 :message "Internal Server Error") 
                    )
                )        
                    
)

(temp-entity Account
        "This entity will return an array of Account"
        (api-docs-url "https://app.churnzero.net/developers#api-reference")
        (source (http/get : url "/Account")
            (setup-test
                        (upon-receiving :code 200 (pass) ; default and thus optional
                                        ))

        )
        (fields
            id : <= "Id"  
            billing_address_city :<= "BillingAddressCity"
            

        
            (Syn-plan
                (change-capture-cursor "ObjectLastModifiedDate"
                    (subset/by-time(query-params 
                                        "startDate" "$FROM" 
                                        "EndDate" "$TO")
                                    (format "yyyy-MM-dd'T'HH:mm:ssZ")
                                    (step-size "24 hr")
                                    (initial  "2023-01-01T00:00:00Z")
                    )
                
                )
            )
        )
)
