import {
  Controller,
  HttpCode,
  Post,
  Req,
  UseGuards,
  Version,
} from '@nestjs/common';
import { RequestFromVidisCore } from '../domain/request-from-vidis-core';
import { LicencesDto } from './LicencesModel';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicencesFromUcsStudentUseCase } from '../usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '../usecases/ucs-student-from-ucs-student-id';
import { ApiKeyGuard } from './authentication/api-key.guard';

@Controller('v1/licences')
@UseGuards(ApiKeyGuard)
export class LicencesController {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenseFetcherService,
  ) {}

  @Post()
  @HttpCode(200)
  @Version('1')
  public getLicences(
    @Req() request: { body: RequestFromVidisCore },
  ): LicencesDto {
    console.log(request);
    // TODO: use correct field for id
    const id = request.body.sub;

    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      id,
    );

    const licencesFromUcs = new LicencesFromUcsStudentUseCase().execute(
      ucsStudent,
    );
    console.log('Test');
    return new LicencesDto(licencesFromUcs);
  }
}
