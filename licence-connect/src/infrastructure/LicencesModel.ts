import { AvailableLicences } from 'domain/licence';

/**
 * Dto for the Licences used as a Response from LicenceConnect
 */
export class LicencesDto {
  constructor(licences: AvailableLicences) {
    this.hasLicence = !!licences;
    this.licences = licences;
  }

  hasLicence: boolean;
  licences: AvailableLicences;
}
