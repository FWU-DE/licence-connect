import { Injectable } from '@nestjs/common';
import { LCLicenses } from '../licence-types';
import { HttpService } from '@nestjs/axios';
import { LicenseService as LicenseFetcherService } from '../licence-service';
import { MVStudent } from '../ucs-types';
import { ucsResponseWithLicences } from '../test_data';
import { MVLicenceService } from './mv-licence.service';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class MVLicenceFetcherService implements LicenseFetcherService {
  constructor(
    private readonly licenceExtractorService: MVLicenceService,
    private readonly httpService: HttpService,
  ) {}

  public getLicenses(id: string): LCLicenses {
    const studentData = this.fetchLicenseDataFromMV(id);
    return this.licenceExtractorService.extractLicenceData(studentData);
  }

  private fetchLicenseDataFromMV(_id: string): MVStudent {
    // TODO: Request real data at UCS@School
    const studentData = ucsResponseWithLicences;
    return studentData;
  }
}
