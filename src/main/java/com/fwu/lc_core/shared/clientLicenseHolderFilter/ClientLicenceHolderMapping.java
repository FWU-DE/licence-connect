package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "licence_holder_mapping")
public class ClientLicenceHolderMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "clientName", unique = true, nullable = false)
    private String clientName;

    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> allowedEntries;
}
