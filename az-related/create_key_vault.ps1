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

#########################################  ARM

New-AzResourceGroupDeployment `
    -Name "Key-Vault-deployment" `
    -ResourceGroupName $resourceGroup `
    -TemplateFile './template/key-vault-template.json' `
    -TemplateParameterFile './template/key-vault-parameters.json'