# Explore the Azure App Configuration

App Configuration offers the following benefits:
- A fully managed service that can be set up in minutes
- Flexible key representations and mappings
- Tagging with labels
- Point-in-time replay of settings
- Dedicated UI for feature flag management
- Comparison of two sets of configurations on custom-defined dimensions
- Enhanced security through Azure-managed identities
- Complete data encryptions, at rest or in transit
- Native integration with popular frameworks

App Configuration complements Azure Key Vault, which is used to store application secrets. App Configuration makes it
easier to implement the following scenarios:
- Centralize management and distribution of hierarchical configuration data for different environments and geographies
- Dynamically change application settings without the need to redeploy or restart an application
- Control feature availability in real-time

## Keys
- Azure App Configuration stores configuration data as key-value pairs
- You can us any unicode character in key names entered into App Configuration except for `*`, `,`, and `\`. These characters are reserved
- If you need to include a reserved character, you must escape it by using `\{Reserved Character}`
- There's a combined size limit of 10,000 characters on a key-value pair. This limit includes all characters in the key, 
  its value and all associated optional attributes
- Key values in App Configuration can optionally have a label attribute. Labels are used to differentiate key values 
  with the same key. A key app1 with labels A and B forms two separate keys in an App Configuration store. By default, 
  the label for a key value is empty, or `null`
```
   Key = AppName:DbEndpoint & Label = Test
   Key = AppName:DbEndpoint & Label = Staging
   Key = AppName:DbEndpoint & Label = Production
```
- You can use labels as a way to create multiple versions of a key value
- You can query an App Configuration store for key values by specifying a pattern

## Manage application features
- Here are several new terms related to feature management:
  - **Feature flag**: A feature flag is a variable with a binary state of on or off. The feature flag also has an associated 
    code block. The state of the feature flag triggers whether the code block runs or not.
  - **Filter**: A filter is a rule for evaluating the state of a feature flag. A user group, a device or browser type,
    a geographic location, and a time window are all examples of what a filter can represent.
  - **Feature manager**: A feature manager is an application package that handles the lifecycle of all the feature flags
    in an application. The feature manager typically provides additional functionality, such as caching feature flags and 
    updating their states.

- Each feature flag has two parts: a name and a list of one or more filters that are used to evaluate if a feature's
  state is on (that is, when its value is True). A filter defines a use case for when a feature should be turned on.
- The App configuration can be used as a **Feature manager** , adding **Filters**
- When a feature flag has multiple filters, the filter list is traversed in order until one of the filters determines 
  the feature should be enabled. At that point, the feature flag is on, and any remaining filter results are skipped. 
  If no filter indicates the feature should be enabled, the feature flag is off
- The feature manager supports appsettings.json as a configuration source for feature flags. The following example 
  shows how to set up feature flags in a JSON file:
```
"FeatureManagement": {
    "FeatureA": true, // Feature flag set to on
    "FeatureB": false, // Feature flag set to off
    "FeatureC": {
        "EnabledFor": [
            {
                "Name": "Percentage",
                "Parameters": {
                    "Value": 50
                }
            }
        ]
    }
}
```

## Secure app configuration data
### Encrypt configuration data by using customer-managed keys
Azure App Configuration encrypts sensitive information at rest using a 256-bit AES encryption key provided by Microsoft. 
Every App Configuration instance has its own encryption key managed by the service and used to encrypt sensitive information. 
Sensitive information includes the values found in key-value pairs. When customer-managed key capability is enabled, 
App Configuration uses a managed identity assigned to the App Configuration instance to authenticate with Azure Active Directory. 
The managed identity then calls Azure Key Vault and wraps the App Configuration instance's encryption key. 
The wrapped encryption key is then stored and the unwrapped encryption key is cached within App Configuration for one hour. 
App Configuration refreshes the unwrapped version of the App Configuration instance's encryption key hourly. 
This ensures availability under normal operating conditions.

### Enable customer-managed key capability
The following components are required to successfully enable the customer-managed key capability for Azure App Configuration:
- Standard tier Azure App Configuration instance
- Azure Key Vault with soft-delete and purge-protection features enabled
- An RSA or RSA-HSM key within the Key Vault: The key must not be expired, it must be enabled, and it must have both 
  wrap and unwrap capabilities enabled

Once these resources are configured, two steps remain to allow Azure App Configuration to use the Key Vault key:
- Assign a managed identity to the Azure App Configuration instance
- Grant the identity GET, WRAP, and UNWRAP permissions in the target Key Vault's access policy

### Use private endpoints for Azure App Configuration
You can use private endpoints for Azure App Configuration to allow clients on a virtual network (VNet) to securely
access data over a private link. The private endpoint uses an IP address from the VNet address space for your
App Configuration store. Network traffic between the clients on the VNet and the App Configuration store traverses
over the VNet using a private link on the Microsoft backbone network, eliminating exposure to the public internet.

Using private endpoints for your App Configuration store enables you to:
- Secure your application configuration details by configuring the firewall to block all connections to App Configuration
  on the public endpoint.
- Increase security for the virtual network (VNet) ensuring data doesn't escape from the VNet.
- Securely connect to the App Configuration store from on-premises networks that connect to the VNet using VPN or
  ExpressRoutes with private-peering.

When creating a private endpoint, you must specify the App Configuration store to which it connects. 
If you have multiple App Configuration stores, you need a separate private endpoint for each store.

We can add managed identities to the App configuration:
- System assigned identity:
```
az appconfig identity assign `
    --resource-group myResourceGroup
    --name myTestAppConfigStore ` 
```
- User assigned identity:
  - create the identity:
```
az identity create `
    --resource-group myResourceGroup `
    --name myUserAssignedIdentity
```
- use the identity:
```
az appconfig identity assign `
    --resource-group myResourceGroup ` 
    --name myTestAppConfigStore ` 
    --identities /subscriptions/[subscription id]/resourcegroups/myResourceGroup/providers/Microsoft.ManagedIdentity/userAssignedIdentities/myUserAssignedIdentity
```