import { IsNotEmpty } from 'class-validator';
import {
  ActivationStatus,
  ValidityStatus,
  Licence,
  LicenseType,
  LicenseSpecialType,
} from '../domain/licence';

export class LicenseStatus {
  assignment_date: number;
  provisioned_date: number;
  status_activation: ActivationStatus;
  status_validity: ValidityStatus;
  validity_start: number;
  validaty_end: number;
}

export class LicenceDto implements Licence {
  @IsNotEmpty()
  license_code: string;
  license_type?: LicenseType;
  license_special_type?: LicenseSpecialType;
  license_status?: LicenseStatus;
}
