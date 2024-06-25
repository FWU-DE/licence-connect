import {
  Body,
  Controller,
  Delete,
  HttpCode,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
  Version,
} from '@nestjs/common';
import { LicencesDto } from './licences/dto/licences.dto';
import { VidisRequestDto } from './licences/dto/vidis-request.dto';
import { AddLicenceRequestDto } from './licences/dto/add-licence-request.dto';
import { InMemoryRepositoryService } from './licences/repository/in-memory-repository.service';
import { ApiOperation, ApiResponse, ApiSecurity } from '@nestjs/swagger';
import { RemoveLicenceRequestDto } from './licences/dto/remove-licence-request.dto';
import { VidisApiKeyGuard } from './authentication/vidis-api-key/vidis-api-key.guard';
import { LicenceManagementApiKeyGuard } from './authentication/licence-management-api-key/licence-management-api-key.guard';

@Controller('v1/licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true }))
export class LicencesController {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}

  @Post('request')
  @HttpCode(200)
  @Version('1')
  @ApiSecurity('VIDIS-Core')
  @UseGuards(VidisApiKeyGuard)
  @ApiOperation({
    description:
      'Request the use for licenses for a specific application. If this request is triggered because of a changed licence, the old licence is not released.',
    tags: ['licences'],
  })
  @ApiResponse({
    status: 200,
    description: 'Fetch all licences available for a specific user',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public getLicences(@Body() body: VidisRequestDto): LicencesDto {
    const id = body.userId;

    const licencesForUser = this.licenceRepository.getLicencesForStudentId(id);

    return new LicencesDto(licencesForUser);
  }

  @Post('release')
  @HttpCode(200)
  @Version('1')
  @ApiSecurity('VIDIS-Core')
  @UseGuards(VidisApiKeyGuard)
  @ApiOperation({
    description:
      'Release one licenses for a specific application used by the specific user',
    tags: ['licences'],
  })
  @ApiResponse({
    status: 200,
    description: 'Successfully released licence if it was available',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public releaseLicences(@Body() body: VidisRequestDto) {
    const id = body.userId;

    console.log(`Try to release a licence for user ${id}`);
  }

  @Post('add')
  @Version('1')
  @ApiSecurity('LicenceManagement')
  @UseGuards(LicenceManagementApiKeyGuard)
  @ApiOperation({
    description: 'Add a specific licence for a the user with the given UserId',
    tags: ['licenceManagement'],
  })
  @ApiResponse({
    status: 201,
    description: 'Save the licencenes as available licences for the specific ',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public addLicences(@Body() body: AddLicenceRequestDto) {
    const id = body.studentId;
    const licencesToAdd = body.licencesToAdd;

    this.licenceRepository.addLicencesForStudentId(id, licencesToAdd);
  }

  @Delete('remove')
  @Version('1')
  @ApiSecurity('LicenceManagement')
  @UseGuards(LicenceManagementApiKeyGuard)
  @ApiOperation({
    description:
      'Remove the specified licence for a the user with the given UserId. If no licences are specified, all licences are released',
    tags: ['licenceManagement'],
  })
  @ApiResponse({
    status: 200,
    description: 'Remove the licencenes for the specific user',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public removeLicences(@Body() body: RemoveLicenceRequestDto) {
    const id = body.studentId;

    if (body.licencesToAdd) {
      this.licenceRepository.removeLicencesForStudentId(id, body.licencesToAdd);
    } else {
      this.licenceRepository.removeAllLicencesForStudentId(id);
    }
  }
}
