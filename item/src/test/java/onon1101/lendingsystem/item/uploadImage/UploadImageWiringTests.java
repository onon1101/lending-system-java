package onon1101.lendingsystem.item.uploadImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageReader;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

class UploadImageWiringTests {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(UploadConfiguration.class)
            .withBean(DatabaseClient.class, () -> mock(DatabaseClient.class))
            .withBean(CurrentUserProvider.class, () -> mock(CurrentUserProvider.class))
            .withBean(IClock.class, () -> mock(IClock.class))
            .withBean(ImageStorage.class, () -> mock(ImageStorage.class));

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = UploadImageController.class, useDefaultFilters = true,
            excludeFilters = @ComponentScan.Filter(Configuration.class))
    static class UploadConfiguration {}

    @Test
    void enabledUploadResolvesControllerServiceAndRepositories() {
        runner.withPropertyValues("lending.rustfs.enabled=true").run(context -> {
            assertNull(context.getStartupFailure());
            assertNotNull(context.getBean(UploadImageController.class));
            assertNotNull(context.getBean(UploadImageService.class));
            assertEquals(1, context.getBeansOfType(UploadImageReader.class).size());
            assertEquals(1, context.getBeansOfType(UploadImageWriter.class).size());
        });
    }

    @Test
    void disabledUploadDoesNotRegisterEndpointOrService() {
        runner.withPropertyValues("lending.rustfs.enabled=false").run(context -> {
            assertNull(context.getStartupFailure());
            assertTrue(context.getBeansOfType(UploadImageController.class).isEmpty());
            assertTrue(context.getBeansOfType(UploadImageService.class).isEmpty());
        });
    }
}
