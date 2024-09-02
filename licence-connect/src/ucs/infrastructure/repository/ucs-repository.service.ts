import { Injectable } from '@nestjs/common';
import { UcsLicenseFetcherService } from '../ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { UcsStudent, UcsStudentId } from '@ucs/domain/ucs-types';
import { UcsStudentRepository } from '@ucs/domain/ucs-student-repository';
import { UcsStudentProvider } from '@ucs/domain/ucs-student-provider';

class UcsStudentRepositoryForUcsStudentProvider
  implements UcsStudentRepository
{
  constructor(
    private readonly ucsStudentProvider: UcsStudentProvider,
    private readonly ucsLicenceFetcherService: UcsLicenseFetcherService,
  ) {}

  public async getUCSStudentFromId(userId: UcsStudentId): Promise<UcsStudent> {
    const ucsStudent = this.ucsLicenceFetcherService.fetchUcsStudentFromId(
      userId,
      this.ucsStudentProvider,
    );

    return await ucsStudent;
  }
}

@Injectable()
export class UcsRepositoryService {
  constructor(
    private readonly ucsLicenceFetcherService: UcsLicenseFetcherService,
  ) {}

  public getUcsStudentRepository(ucsStudentProvider: UcsStudentProvider) {
    return new UcsStudentRepositoryForUcsStudentProvider(
      ucsStudentProvider,
      this.ucsLicenceFetcherService,
    );
  }
}
