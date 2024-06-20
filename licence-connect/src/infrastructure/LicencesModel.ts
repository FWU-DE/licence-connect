import { AvailableLicenses } from '../domain/licence';

export class LicensesModel {
  constructor(licences: AvailableLicenses) {
    this.hasLicense = !!licences;
    this.licenses = licences;
  }

  hasLicense: boolean;
  licenses: AvailableLicenses;
}
