package com.georgeciachir.keyvaultexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/configuration")
public class ConfigController {

    private static final Logger LOG = LoggerFactory.getLogger(SeecretsKeyVaultController.class);

    @GetMapping
    public Map<String, String> retrieveSecret() {
        LOG.info("Retrieving the configuration values");
        return System.getenv().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("APP_CONFIG"))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
