import {
  Body,
  Controller,
  HttpCode,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
  Version,
} from '@nestjs/common';
import { LicencesDto } from './licences/dto/licences.dto';
import { ApiKeyGuard } from './authentication/api-key.guard';
import { VidisRequestRequest } from './licences/dto/vidis-request.dto';
import { InMemoryRepositoryService } from './licences/repository/in-memory-repository.service';
import { AddLicenceRequestDto } from './licences/dto/add-licence-request.dto';

@Controller('v1/licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true }))
@UseGuards(ApiKeyGuard)
export class LicencesController {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}

  @Post()
  @HttpCode(200)
  @Version('1')
  public getLicences(@Body() body: VidisRequestRequest): LicencesDto {
    const id = body.userId;

    const licencesForUser = this.licenceRepository.getLicencesForStudentId(id);

    return new LicencesDto(licencesForUser);
  }

  @Post('add')
  @Version('1')
  public addLicences(@Body() body: AddLicenceRequestDto) {
    const id = body.studentId;
    const licencesToAdd = body.licencesToAdd;

    this.licenceRepository.addLicencesForStudentId(id, licencesToAdd);
  }
}
