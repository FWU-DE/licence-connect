import { AvailableLicences } from '@vidis/domain/licence';
import { Type } from 'class-transformer';
import { IsBoolean } from 'class-validator';

/**
 * Dto for providing the licences to VIDIS Kern
 */
export class LicencesDto {
  constructor(licences: AvailableLicences) {
    this.hasLicence = licences.length !== 0;
    this.licences = licences;
  }

  @IsBoolean()
  hasLicence: boolean;
  @Type(() => LicencesDto)
  licences: AvailableLicences;
}
