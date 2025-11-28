package io.interfero;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTest
{
    @Test
    void verifyAndDocumentModules()
    {
        var modules = ApplicationModules.of(InterferoApplication.class);
        modules.verify();

        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleMetadata()
                .writeModuleCanvases();
    }
}
