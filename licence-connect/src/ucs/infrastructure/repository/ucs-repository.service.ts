import { Injectable } from '@nestjs/common';
import { UCSLicenseFetcherService } from '../ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicenceStrategy } from '@vidis/domain/licence-strategy';
import { AvailableLicences } from '@vidis/domain/licence';
import { LicencesFromUcsStudentUseCase } from '@ucs/usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '@ucs/usecases/ucs-student-from-ucs-student-id';

@Injectable()
export class UcsRepositoryService implements LicenceStrategy {
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
