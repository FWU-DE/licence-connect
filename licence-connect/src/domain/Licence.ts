export type LicenceIdentifier = string;

type LicenseType = 'single' | 'volumnes';
type LicenseSpecialType = 'none';
type LicenseStatus = {
  assignment_date?: number;
  provisioned_date?: number;
  status_activation?: 'ACTIVATED';
  status_validity?: 'VALID';
  validity_start?: number;
  validity_end?: number;
};
export type Licence = {
  license_code: LicenceIdentifier;
  license_type?: LicenseType;
  license_special_type?: LicenseSpecialType;
  license_status?: LicenseStatus;
};

export type AvailableLicences = Licence[];
