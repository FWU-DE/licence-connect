import { Controller, Get, Req } from '@nestjs/common';
import { RequestFromVidisCore } from '../domain/request-from-vidis-core';
import { LicensesModel } from './LicencesModel';
import { UCSLicenceFetcherService } from '../infrastructure/ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicencesFromUcsStudendUseCase } from '../usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '../usecases/ucs-student-from-ucs-student-id';

@Controller('licences')
export class LicencesController {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenceFetcherService,
  ) {}

  // TODO: Add Authentication
  @Get()
  public getLicences(
    @Req() request: { body: RequestFromVidisCore },
  ): LicensesModel {
    // TODO: use correct field for id
    const id = request.body.sub;

    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      id,
    );

    const licencesFromUcs = new LicencesFromUcsStudendUseCase().execute(
      ucsStudent,
    );
    return new LicensesModel(licencesFromUcs);
  }
}
