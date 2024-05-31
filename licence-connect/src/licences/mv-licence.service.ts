import { Injectable } from '@nestjs/common';
import { LCLicences } from './licence-types';
import { HttpService } from '@nestjs/axios';
import { LicenceService } from './licence-service';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class MVLicenceService implements LicenceService {
  constructor(private readonly httpService: HttpService) {}

  public getLicences(_id: string): LCLicences {
    // TODO: Request real data at UCS@School
    return { hasLicence: false, licences: [] };
  }

  private getAuthentificationToken(): string {
    return '';
  }
}
