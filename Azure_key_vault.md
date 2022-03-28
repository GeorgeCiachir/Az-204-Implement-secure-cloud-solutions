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
        - create an App Service application
        - create an identity for this app service -> in the app service page, go to the Identity section. There are 2 options here
          - "System assigned" identity tab, toggle the status to ON
          - "User assigned" identity tab -> for this, a managed identity needs to be previously created and added here. 
             There is a "Managed Identity" Azure service for this
          - in either case, get the created object id of the newly created identity
        - go back to the key vault and add a policy for the newly created app service. Basically add te previously created identity
          and add some permissions for accessing the key vault
        - deploy the app in App Service. That's it, and it all works because the App Service app is registered with the key vault
     2. The application can access the vault from outside Azure, using an `AzureCredentialBuilder`
        - for this to be possible, we need to register the application with Azure AD
        - in AAD go to App registrations, and register a new app
        - go to the "Certificates & secrets" page of the newly registered app, and create a new secret (a password)
        - wherever you deploy the app, set this environment variables (all of them can be accessed via the overview page of the newly registered app)
             - AZURE_CLIENT_ID=the newly registered app id
             - AZURE_TENANT_ID=the tenant id (not sure if this is the tenant id of the app, or the one of the key vault)
             - AZURE_CLIENT_SECRET=the newly created app password
        - Because I used the `DefaultAzureCredentialBuilder` which, on the build method also creates a `DefaultAzureCredential`. 
          Among those credentials there is a `EnvironmentCredential` which is added and this credential relies on the
          mentioned environment variables (it can also construct the credentials based on other values, such as AZURE_CLIENT_CERTIFICATE_PATH).  
          See the class definition
        - this means that if the App Service application has these env variables in the configuration properties, it 
          doesn't event needs to be registered with the key vault as previously done. The problem with this approach is that 
          the credentials ust be stored in the repo, which of course, is a bad idea
         
- Of course, a combination of the 2 above, is possible, so that the app can be accessed from both within and outside Azure

## Using key vault references
- Problem: 
  - we have some application config properties that are sensitive, and we don't want to expose them on the repository
- Solution: 
  - we now we can store secrets in the key vault
  - create a secret in the key vault with the sensitive info   
  - in the app configuration page, instead of setting the value of the property, set a reference to the newly created 
    key vault entry. there is a special syntax to create this reference:
    `@Microsoft.KeyVault(SecretUri=https://myvault.vault.azure.net/secrets/mysecret/)`
    or `@Microsoft.KeyVault(VaultName=myvault;SecretName=mysecret)`
  - the version of the secret can also be included in the reference string
  - additional details [here](https://docs.microsoft.com/en-us/azure/app-service/app-service-key-vault-references)

## Key vault data soft delete and purge protection
- soft delete is enabled for all new key vaults. For existing ones it has to be manually enabled
- after the item is deleted, it has a retention policy of a min of 7 to a max of 90 days 
- after the item is deleted, it can be immediately purged manually (permanently deleted), or automatically purged,
  after the retention period has passed
- Azure key vault purge protection: when purge protection is enabled, a vault or an object in the deleted state cannot
  be purged util the retention period has passed
- once the purge protection is enabled, it cannot be disabled
- if not enabled from the start, at the vault creation, purge protection can also be later enabled

## Working with keys
- in the key vault we have the option to import a key, generate one or restore one
- this key can be used with other Azure services for encryption purposes (encrypt DM disks, storage accounts and so on).
  For example, remember that the storage account encrypts data at rest. By default, the encryption key is generated and 
  managed by Azure, but we can change that in the "Encryption" section in the Storage Account and set our own encryption key, 
  by specifying an url where to find it, or by selecting it from the vault. Of course, if the key is later purged,
  the Storage Account data becomes inaccessible
- Because of the dangers of purging the key, soft delete and purge protection are automatically enable on the key vault
  when allocating a key for encryption
- Also, the storage itself is registered with the key vault and receives an application access policy