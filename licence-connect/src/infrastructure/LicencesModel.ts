import { AvailableLicenses } from '../domain/Licence';

export class LicensesModel {
  constructor(licences: AvailableLicenses) {
    this.hasLicense = !!licences;
    this.licenses = licences;
  }

  hasLicense: boolean;
  licenses: AvailableLicenses;
}
