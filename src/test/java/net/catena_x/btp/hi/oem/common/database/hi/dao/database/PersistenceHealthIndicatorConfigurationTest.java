package net.catena_x.btp.hi.oem.common.database.hi.dao.database;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(profiles = "dataupdater")
@ContextConfiguration(classes = {PersistenceHealthIndicatorConfiguration.class})
@TestPropertySource(locations = {"classpath:test-healthindicatordb.properties"})
@ComponentScan(basePackages = {"net.catena_x.btp.hi.oem.common.database.hi.dao.database"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersistenceHealthIndicatorConfigurationTest {

    @Autowired LocalContainerEntityManagerFactoryBean healthindicatorEntityManager;
    @Autowired DataSource healthindicatorDataSource;
    @Autowired PlatformTransactionManager healthindicatorTransactionManager;

    @Test
    void injectedComponentsAreNotNull() {
        Assert.assertNotNull(healthindicatorEntityManager);
        Assert.assertNotNull(healthindicatorDataSource);
        Assert.assertNotNull(healthindicatorTransactionManager);
    }
}
