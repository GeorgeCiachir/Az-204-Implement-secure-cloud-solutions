package com.georgeciachir.keyvaultexample.controller;

import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.keys.KeyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificates")
public class CertificateKeyVaultController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateKeyVaultController.class);

    @Autowired
    private CertificateClient certificateClient;

    @GetMapping(value = "/{name}")
    public String retrieveKeyIdByName(@PathVariable String name) {
        LOG.info("Retrieving a certificate");
        return certificateClient.getCertificate(name).getId();
    }
}
