import {
  Body,
  Controller,
  HttpCode,
  Post,
  UseGuards,
  Version,
} from '@nestjs/common';
import { LicencesDto } from './dto/licences.dto';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { LicencesFromUcsStudentUseCase } from '../usecases/licences-from-ucs-student-use-case';
import { UCSStudentFromUCSStudentId } from '../usecases/ucs-student-from-ucs-student-id';
import { ApiKeyGuard } from './authentication/api-key.guard';
import { VidisRequestRequest } from './dto/vidis-request.dto';

@Controller('licences')
@UseGuards(ApiKeyGuard)
export class LicencesController {
  constructor(
    private readonly ucsLicenceFetcherService: UCSLicenseFetcherService,
  ) {}

  @Post()
  @HttpCode(200)
  @Version('1')
  public getLicences(@Body() body: VidisRequestRequest): LicencesDto {
    // TODO: use correct field for id
    const id = body.userId;

    const ucsStudent = new UCSStudentFromUCSStudentId().execute(
      this.ucsLicenceFetcherService,
      id,
    );

    const licencesFromUcs = new LicencesFromUcsStudentUseCase().execute(
      ucsStudent,
    );
    return new LicencesDto(licencesFromUcs);
  }
}
