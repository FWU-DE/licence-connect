import {
  Body,
  Controller,
  Delete,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
  Version,
} from '@nestjs/common';
import { ApiSecurity, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { LicenceManagementApiKeyGuard } from '@licence-management/infrastructure/authentication/licence-management-api-key.guard';
import { AddLicenceRequestDto } from '@licence-management/infrastructure/dto/add-licence-request.dto';
import { RemoveLicenceRequestDto } from '@licence-management/infrastructure/dto/remove-licence-request.dto';
import { Licence } from '@vidis/domain/licence';
import { LicenceDto, createLicenceFromLicenceDto } from './dto/licence.dto';
import { InMemoryRepositoryService } from '@licence-management/infrastructure/repository/in-memory-repository.service';
import { createRemoveLicenceForStudentUseCase } from '@licence-management/usecases/remove-licences-for-student-use-case';
import { createAddLicenceForStudentUseCase } from '@licence-management/usecases/add-licences-for-student-use-case';

@Controller('licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true, whitelist: true }))
export class LicenceManagementController {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
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
    description: 'Save the licences as available licences for the specific ',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public addLicences(@Body() body: AddLicenceRequestDto) {
    const id = body.studentId;
    const licencesToAdd = body.licencesToAdd;

    const licences: Licence[] = this.mapLicenceDtosToLicences(licencesToAdd);

    const useCase = createAddLicenceForStudentUseCase(this.licenceRepository);

    useCase(id, licences);
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
    description: 'Remove the licences for the specific user',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public removeLicences(@Body() body: RemoveLicenceRequestDto) {
    const studentId = body.studentId;
    const licencesToRemove = body.licencesToRemove;

    const useCase = createRemoveLicenceForStudentUseCase(
      this.licenceRepository,
    );

    const areLicencesProvided = licencesToRemove !== undefined;
    if (areLicencesProvided) {
      const providedLicences = this.mapLicenceDtosToLicences(licencesToRemove);
      useCase(studentId, providedLicences);
    } else {
      useCase(studentId, undefined);
    }
  }

  private mapLicenceDtosToLicences(licencesDto: LicenceDto[]) {
    const licences: Licence[] = licencesDto.map((licenceDto) =>
      createLicenceFromLicenceDto(licenceDto),
    );

    return licences;
  }
}
