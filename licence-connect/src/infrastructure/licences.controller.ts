import {
  Body,
  Controller,
  HttpCode,
  Post,
  UseGuards,
  Version,
} from '@nestjs/common';
import { LicencesDto } from './dto/licences.dto';
import { ApiKeyGuard } from './authentication/api-key.guard';
import { VidisRequestRequest } from './dto/vidis-request.dto';
import { UcsRepositoryService } from './ucs/repository/ucs-repository.service';

@Controller('licences')
@UseGuards(ApiKeyGuard)
export class LicencesController {
  constructor(private readonly ucsRepostiory: UcsRepositoryService) {}

  @Post()
  @HttpCode(200)
  @Version('1')
  public getLicences(@Body() body: VidisRequestRequest): LicencesDto {
    const id = body.userId;

    const licencesFromUcs = this.ucsRepostiory.getLicencesForStudentId(id);

    return new LicencesDto(licencesFromUcs);
  }
}
