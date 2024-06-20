import { Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import {
  ResponseFromUCS,
  UCSStudent,
  UCSStudentId,
} from 'domain/ucs/ucs-types';
import { ucsResponseWithLicences } from 'domain/ucs/example-data';
import { IUCSLicenceSource as IUCSStudentSource } from 'domain/ucs/ucs-licences-source';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class UCSLicenceFetcherService implements IUCSStudentSource {
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
