import { AvailableLicences } from '@licences/domain/licence';
import { Type } from 'class-transformer';

/**
 * Dto for providing the licences to VIDIS Kern
 */
export class LicencesDto {
  constructor(licences: AvailableLicences) {
    this.hasLicence = licences.length !== 0;
    this.licences = licences;
  }

  hasLicence: boolean;
  @Type(() => LicencesDto)
  licences: AvailableLicences;
}
