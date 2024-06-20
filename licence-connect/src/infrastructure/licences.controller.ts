import { Controller, Get, Req, UseGuards } from '@nestjs/common';
import { RequestFromVidisCore } from '../domain/request-from-vidis-core';
import { LicencesDto } from './LicencesModel';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicencesFromUcsStudendUseCase } from '../usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '../usecases/ucs-student-from-ucs-student-id';
import { ApiKeyGuard } from './authentication/api-key.guard';

@Controller('licences')
@UseGuards(ApiKeyGuard)
export class LicencesController {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenseFetcherService,
  ) {}

  @Get()
  public getLicences(
    @Req() request: { body: RequestFromVidisCore },
  ): LicencesDto {
    // TODO: use correct field for id
    const id = request.body.sub;

    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      id,
    );

    const licencesFromUcs = new LicencesFromUcsStudendUseCase().execute(
      ucsStudent,
    );
    return new LicencesDto(licencesFromUcs);
  }
}
