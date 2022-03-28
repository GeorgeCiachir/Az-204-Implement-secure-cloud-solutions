# Azure key vault

## What to store:
- keys -> cryptographic keys
- secrets -> sensitive connections strings or passwords
- certificates used for Https/TLS

## Various options
- in order to access the vault, access policies need to be set for the users that will access the vault
- if the vault is created from the CLI, no access policies will be set, and they need to be added manually

## Creating an app that uses the vault
- The application in this project is created mostly based on [this page](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/keyvault/azure-security-keyvault-secrets#create-secret-client) 
- Steps:
  1. create the vault and from the overview page grab the vault url, needed for the app
  2. we then need to register this application to the vault. There are several ways to do this:
     1. The application can access the key vault only from within Azure (using managed identities)
        - create an app service
        - create an identity for this app service -> in the app service page, go to the Identity section. There are 2 options here
          - "System assigned" identity tab, toggle the status to ON
          - "User assigned" identity tab -> for this, a managed identity needs to be previously created and added here. There is a "Managed Identity" Azure service for this
          - in either case, get the created object id of the newly created identity
        - go back to the key vault and add a policy for the newly created app service. Basically add te previously created identity
          and add some permissions for accessing the key vault
        - deploy the app in App Service. That's it, and it all works because the App Service app is registered with the key vault
     2. The application can access the vault from outside Azure
        - for this to be possible, we need to register the application with Azure AD
        - in AAD go to App registrations, and register a new app
        - go to the "Certificates & secrets" page of the newly registered app, and create a new secret (a password)
        - wherever you deploy the app, set this environment variables (all of them can be accessed via the overview page of the newly registered app)
             - AZURE_CLIENT_ID=the newly registered app id
             - AZURE_TENANT_ID=the tenant id (not sure if this is the tenant id of the app, or the one of the key vault)
             - AZURE_CLIENT_SECRET=the newly created app password
- Of course, a combination of the 2 above, is possible, so that the app can be accessed from both within and outside Azure