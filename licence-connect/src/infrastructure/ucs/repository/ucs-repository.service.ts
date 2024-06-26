import { Injectable } from '@nestjs/common';
import { UCSLicenseFetcherService } from '../ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicencesFromUcsStudentUseCase } from '@usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '@usecases/ucs-student-from-ucs-student-id';
import { AvailableLicences } from '@domain/licence';
import { LicenceSource } from '@domain/licence-source';

@Injectable()
export class UcsRepositoryService implements LicenceSource {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenseFetcherService,
  ) {}

  public getLicencesForStudentId(userId: string): AvailableLicences {
    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      userId,
    );

    const licencesFromUcs = new LicencesFromUcsStudentUseCase().execute(
      ucsStudent,
    );

    return licencesFromUcs;
  }
}
