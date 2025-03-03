package com.fwu.lc_core.clientLicenseHolderFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ClientLicenseHolderFilterServiceTests {

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;

    @BeforeEach
    public void init(){
        clientLicenceHolderMappingRepository.deleteAll();
    }

    @Test
    public void defaultAllowedSystemsAreEmpty() {
        String requesterName = "dummy requesting system name";

        EnumSet<AvailableLicenceHolders> allowedSystems = clientLicenseHolderFilterService.getAllowedLicenceHolders(requesterName);

        assertThat(allowedSystems).isEmpty();
    }

    @Test
    public void configureRequesterWithAvailableLicenceHolderNameWorks() throws Exception {
        String requesterName = "dummy requesting system name";
        EnumSet<AvailableLicenceHolders> allowedEntries = EnumSet.of(AvailableLicenceHolders.ARIX);

        clientLicenseHolderFilterService.setAllowedLicenceHolders(requesterName, allowedEntries);

        EnumSet<AvailableLicenceHolders> allowedSystems = clientLicenseHolderFilterService.getAllowedLicenceHolders(requesterName);
        assertThat(allowedSystems).containsExactlyInAnyOrderElementsOf(allowedEntries);
    }
}
