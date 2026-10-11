package onon1101.lendingsystem.item.uploadCover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageReader;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.infrastructure.image.ImageValidator;
import onon1101.lendingsystem.item.uploadCover.repository.UploadCoverReader;
import onon1101.lendingsystem.item.uploadCover.repository.UploadCoverWriter;
import onon1101.lendingsystem.shared.time.IClock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

class UploadCoverWiringTests {
    private final ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withUserConfiguration(UploadConfiguration.class)
                    .withBean(ImageValidator.class, ImageValidator::new)
                    .withBean(ImageReader.class)
                    .withBean(DatabaseClient.class, () -> mock(DatabaseClient.class))
                    .withBean(CurrentUserProvider.class, () -> mock(CurrentUserProvider.class))
                    .withBean(IClock.class, () -> mock(IClock.class))
                    .withBean(ImageStorage.class, () -> mock(ImageStorage.class));

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(
            basePackageClasses = UploadCoverController.class,
            useDefaultFilters = true,
            excludeFilters = @ComponentScan.Filter(Configuration.class))
    static class UploadConfiguration {}

    @Test
    void enabledUploadResolvesControllerServiceAndRepositories() {
        runner.withPropertyValues("lending.rustfs.enabled=true")
                .run(
                        context -> {
                            assertNull(context.getStartupFailure());
                            assertNotNull(context.getBean(UploadCoverController.class));
                            assertNotNull(context.getBean(UploadCoverService.class));
                            assertEquals(1, context.getBeansOfType(UploadCoverReader.class).size());
                            assertEquals(1, context.getBeansOfType(UploadCoverWriter.class).size());
                        });
    }

    @Test
    void disabledUploadDoesNotRegisterEndpointOrService() {
        runner.withPropertyValues("lending.rustfs.enabled=false")
                .run(
                        context -> {
                            assertNull(context.getStartupFailure());
                            assertTrue(
                                    context.getBeansOfType(UploadCoverController.class).isEmpty());
                            assertTrue(context.getBeansOfType(UploadCoverService.class).isEmpty());
                        });
    }
}
