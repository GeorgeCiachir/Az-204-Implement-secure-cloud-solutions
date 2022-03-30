$vaultName = "Az204-Vault"
$resourceGroup = "implementingsecureapps"
$location = "westeurope"


#########################################  PowerShell

New-AzKeyVault `
    -VaultName $vaultName `
    -ResourceGroupName $resourceGroup `
    -Location $location

#########################################  Az cli

az keyvault create `
    --name $vaultName `
    --resource-group $resourceGroup `
    --location $location

az keyvault secret set `
    --vault-name $vaultName `
    --name "password" `
    --value "1234"

az keyvault secret show `
    --vault-name $vaultName `
    --name "password"

#########################################  ARM

New-AzResourceGroupDeployment `
    -Name "Key-Vault-deployment" `
    -ResourceGroupName $resourceGroup `
    -TemplateFile './template/key-vault-template.json' `
    -TemplateParameterFile './template/key-vault-parameters.json'