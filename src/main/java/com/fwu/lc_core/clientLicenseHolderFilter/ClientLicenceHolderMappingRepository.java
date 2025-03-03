package com.fwu.lc_core.clientLicenseHolderFilter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientLicenceHolderMappingRepository extends JpaRepository<ClientLicenceHolderMapping, Long> {
    Optional<ClientLicenceHolderMapping> findByRequesterName(String requestingClientName);
}
