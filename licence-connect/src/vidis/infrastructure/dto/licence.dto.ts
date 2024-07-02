import {
  ActivationStatus,
  ValidityStatus,
  Licence,
  LicenseType,
  LicenseSpecialType,
} from '@licences/domain/licence';
import { IsNotEmpty } from 'class-validator';

export class LicenseStatus {
  assignment_date?: number;
  provisioned_date?: number;
  status_activation?: ActivationStatus;
  status_validity?: ValidityStatus;
  validity_start?: number;
  validaty_end?: number;
}

export const createLicenceDto = (licence: Licence) => {
  const licenceDto = new LicenceDto();
  licenceDto.license_code = licence.license_code;
  licenceDto.license_special_type = licence.license_special_type;
  licenceDto.license_status = licence.license_status;
  licenceDto.license_type = licence.license_type;
  return licenceDto;
};

export class LicenceDto {
  @IsNotEmpty()
  license_code!: string;
  license_type?: LicenseType;
  license_special_type?: LicenseSpecialType;
  license_status?: LicenseStatus;
}
