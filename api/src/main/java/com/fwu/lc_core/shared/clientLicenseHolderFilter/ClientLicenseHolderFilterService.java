package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fwu.lc_core.licences.models.LicenceHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
public class ClientLicenseHolderFilterService {

    @Autowired
    private ClientLicenceHolderMappingRepository repository;

    public EnumSet<LicenceHolder> getAllowedLicenceHolders(String clientName) {
        Optional<ClientLicenceHolderMapping> mapping = repository.findByClientName(clientName);
        return mapping.map(m -> EnumSetConverter.toEnumSet(m.getAllowedEntries())).orElse(EnumSet.noneOf(LicenceHolder.class));
    }

    public void setAllowedLicenceHolders(String clientName, EnumSet<LicenceHolder> allowedEntries) {
        Optional<ClientLicenceHolderMapping> existingMapping = repository.findByClientName(clientName);
        ClientLicenceHolderMapping mapping;
        if (existingMapping.isPresent()) {
            mapping = existingMapping.get();
        } else {
            mapping = new ClientLicenceHolderMapping();
            mapping.setClientName(clientName);
        }
        mapping.setAllowedEntries(EnumSetConverter.toStringSet(allowedEntries));
        repository.save(mapping);
    }
}
