import { Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ResponseFromUCS } from '../UCSTypes';
import { ucsResponseWithLicences } from '../test/example_data';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class MVLicenceFetcherService {
  constructor(private readonly _httpService: HttpService) {}

  public getLicenses(id: string): ResponseFromUCS {
    const studentData = this.fetchLicenseDataFromMV(id);
    return studentData;
  }

  private fetchLicenseDataFromMV(_id: string): ResponseFromUCS {
    // TODO: Request real data at UCS@School
    const studentData = ucsResponseWithLicences;
    return studentData;
  }
}
