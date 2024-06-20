import { AvailableLicenses } from './licence';

export interface LicenseService {
  getLicenses(userId: string): AvailableLicenses;
}
