import { AvailableLicences } from './licence';

export interface LicenceService {
  getLicences(userId: string): AvailableLicences;
}
