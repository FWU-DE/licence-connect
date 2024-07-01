import { Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ucsResponseWithLicences } from '@ucs/domain/example-data';
import {
  UCSStudentId,
  UCSStudent,
  ResponseFromUCS,
} from '@ucs/domain/ucs-types';
import { UCSLicenceSource } from '@ucs/domain/ucs-licences-source';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class UCSLicenseFetcherService implements UCSLicenceSource {
  constructor(private readonly _httpService: HttpService) {}

  public getUCSStudentFromId(id: UCSStudentId): UCSStudent {
    const studentData = this.fetchLicenseDataFromUCS(id);
    return this.extractStudentFromResponse(studentData);
  }

  private extractStudentFromResponse(response: ResponseFromUCS): UCSStudent {
    const student = response['http://www.bildungslogin.de/licenses'];
    return student;
  }

  private fetchLicenseDataFromUCS(_id: string): ResponseFromUCS {
    // TODO: Request real data at UCS@School
    const studentData = ucsResponseWithLicences;
    return studentData;
  }
}
