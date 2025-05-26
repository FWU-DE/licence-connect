package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientLicenceHolderMappingRepository extends JpaRepository<ClientLicenceHolderMapping, Long> {
    Optional<ClientLicenceHolderMapping> findByClientName(String requestingClientName);
}
