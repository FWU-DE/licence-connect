import {
  ActivationStatus,
  ValidityStatus,
  Licence,
  LicenseType,
  LicenseSpecialType,
  licenceTypes,
  licenceSpecialTypes,
  activationStatus,
  validityStatus,
} from '@licences/domain/licence';
import { IsIn, IsNotEmpty, IsNumber, IsOptional } from 'class-validator';

export class LicenseStatus {
  @IsOptional()
  @IsNumber()
  assignment_date?: number;

  @IsOptional()
  @IsNumber()
  provisioned_date?: number;

  @IsOptional()
  @IsIn(activationStatus)
  status_activation?: ActivationStatus;

  @IsOptional()
  @IsIn(validityStatus)
  status_validity?: ValidityStatus;

  @IsOptional()
  @IsNumber()
  validity_start?: number;

  @IsOptional()
  @IsNumber()
  validaty_end?: number;
}

export class LicenceDto {
  @IsNotEmpty()
  license_code!: string;

  @IsOptional()
  @IsIn(licenceTypes)
  license_type?: LicenseType;

  @IsOptional()
  @IsIn(licenceSpecialTypes)
  license_special_type?: LicenseSpecialType;

  @IsOptional()
  license_status?: LicenseStatus;
}

export const createLicenceDtoFromLicence = (licence: Licence) => {
  const licenceDto = new LicenceDto();
  licenceDto.license_code = licence.license_code;
  licenceDto.license_special_type = licence.license_special_type;
  licenceDto.license_status = licence.license_status;
  licenceDto.license_type = licence.license_type;
  return licenceDto;
};

export const createLicenceFromLicenceDto = (licenceDto: LicenceDto) => {
  const licence = {
    license_code: licenceDto.license_code,
    license_special_type: licenceDto.license_special_type,
    license_status: licenceDto.license_status,
    license_type: licenceDto.license_type,
  };
  return licence;
};
