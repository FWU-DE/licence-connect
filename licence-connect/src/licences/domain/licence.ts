import { EducationalOfferId } from './educational-offer';

export type LicenceIdentifier = string;

const licenceTypes = ['single', 'volumnes'] as const;
export type LicenseType = (typeof licenceTypes)[number];

const licenceSpecialTypes = ['none'] as const;
export type LicenseSpecialType = (typeof licenceSpecialTypes)[number];

const activationStatus = ['ACTIVATED'] as const;
export type ActivationStatus = (typeof activationStatus)[number];

const validityStatus = ['VALID'] as const;
export type ValidityStatus = (typeof validityStatus)[number];

type LicenseStatus = {
  assignment_date?: number;
  provisioned_date?: number;
  status_activation?: ActivationStatus;
  status_validity?: ValidityStatus;
  validity_start?: number;
  validity_end?: number;
};
export type Licence = {
  license_code: LicenceIdentifier;
  educationalOfferId: EducationalOfferId;
  license_type?: LicenseType;
  license_special_type?: LicenseSpecialType;
  license_status?: LicenseStatus;
};

export type AvailableLicences = Licence[];
