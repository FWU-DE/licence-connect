import { AvailableLicenses } from './Licence';

export interface LicenseService {
  getLicenses(userId: string): AvailableLicenses;
}
