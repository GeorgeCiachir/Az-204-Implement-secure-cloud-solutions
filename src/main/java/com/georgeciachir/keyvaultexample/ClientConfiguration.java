package com.georgeciachir.keyvaultexample;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    private static final String KEY_VAULT_URI = "https://securingappskv.vault.azure.net";

    @Bean
    public SecretClient secretClient() {
        return new SecretClientBuilder()
                .vaultUrl(KEY_VAULT_URI)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    public KeyClient keyClient() {
        return new KeyClientBuilder()
                .vaultUrl(KEY_VAULT_URI)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }
}
