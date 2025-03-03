package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
public class ClientLicenseHolderFilterService {

    @Autowired
    private ClientLicenceHolderMappingRepository repository;

    public EnumSet<AvailableLicenceHolders> getAllowedLicenceHolders(String requesterName) {
        Optional<ClientLicenceHolderMapping> mapping = repository.findByRequesterName(requesterName);
        return mapping.map(m -> EnumSetConverter.toEnumSet(m.getAllowedEntries())).orElse(EnumSet.noneOf(AvailableLicenceHolders.class));
    }

    public void setAllowedLicenceHolders(String requesterName, EnumSet<AvailableLicenceHolders> allowedEntries) throws Exception {
        ClientLicenceHolderMapping mapping = new ClientLicenceHolderMapping();
        mapping.setRequesterName(requesterName);
        mapping.setAllowedEntries(EnumSetConverter.toStringSet(allowedEntries));
        repository.save(mapping);
    }
}
