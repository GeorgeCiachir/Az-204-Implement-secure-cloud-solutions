package com.georgeciachir.keyvaultexample.controller;

import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.DeletedSecret;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.georgeciachir.keyvaultexample.AddSecretCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/secrets")
public class SecretsKeyVaultController {

    private static final Logger LOG = LoggerFactory.getLogger(SecretsKeyVaultController.class);

    @Autowired
    private SecretClient secretClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void addSecret(@RequestBody AddSecretCommand addSecretCommand) {
        LOG.info("Adding secret");
        secretClient.setSecret(new KeyVaultSecret(addSecretCommand.name, addSecretCommand.value)
                .setProperties(new SecretProperties()
                        .setExpiresOn(OffsetDateTime.now().plusYears(1))));
    }

    @GetMapping(value = "/{name}")
    public String retrieveSecret(@PathVariable String name) {
        LOG.info("Retrieving a secret");
        return secretClient.getSecret(name).getValue();
    }

    @DeleteMapping(value = "/{name}")
    public void delete(@PathVariable String name) {
        LOG.info("Deleting a secret");
        SyncPoller<DeletedSecret, Void> deletedBankSecretPoller
                = secretClient.beginDeleteSecret(name);

        PollResponse<DeletedSecret> deletedBankSecretPollResponse = deletedBankSecretPoller.poll();

        LOG.info("Deleted Date {}" + deletedBankSecretPollResponse.getValue().getDeletedOn().toString());
        LOG.info("Deleted Secret's Recovery Id {}", deletedBankSecretPollResponse.getValue().getRecoveryId());

        // Key is being deleted on server.
        deletedBankSecretPoller.waitForCompletion();

        // If the key vault is soft-delete enabled, then for permanent deletion  deleted secrets need to be purged.
        secretClient.purgeDeletedSecret("name");
    }
}
