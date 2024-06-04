import { LCLicenses } from './licence-types';

export interface LicenseService {
  getLicenses(userId: string): LCLicenses;
}
