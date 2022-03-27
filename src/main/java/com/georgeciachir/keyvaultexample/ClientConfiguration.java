package com.georgeciachir.keyvaultexample;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public SecretClient secretClient() {
        return new SecretClientBuilder()
                .vaultUrl("https://securingappskv.vault.azure.net")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }
}
