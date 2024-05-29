import { LCLicences } from './licence-types';

export interface LicenceService {
  getLicences(userId: string): LCLicences;
}
