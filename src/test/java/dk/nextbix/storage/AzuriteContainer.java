package dk.nextbix.storage;

import org.testcontainers.containers.GenericContainer;

public class AzuriteContainer {

    public static GenericContainer getInstance() {
        return new GenericContainer("mcr.microsoft.com/azure-storage/azurite");
    }

}