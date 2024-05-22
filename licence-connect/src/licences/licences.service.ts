import { Injectable } from '@nestjs/common';
import { Licences } from './types';

@Injectable()
export class LicenceService {
  getLicences(): Licences {
    return { hasLicence: false, licences: [] };
  }
}
