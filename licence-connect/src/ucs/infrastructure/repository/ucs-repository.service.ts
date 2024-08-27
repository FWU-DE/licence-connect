import { Injectable } from '@nestjs/common';
import { UCSLicenseFetcherService } from '../ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { UCSStudentFromUCSStudentId } from '@ucs/usecases/ucs-student-from-ucs-student-id';
import { UCSStudent } from '@ucs/domain/ucs-types';

@Injectable()
export class UcsRepositoryService {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenseFetcherService,
  ) {}

  public getLicenceObjectForStudentId(userId: string): UCSStudent {
    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      userId,
    );

    return ucsStudent;
  }
}
