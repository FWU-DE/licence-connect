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
import { AddLicenceForStudentUseCaseFactoryService } from '@licence-management/infrastructure/usecase-factories/add-licence-for-student-use-case-factory.service';
import { RemoveLicenceForStudentUseCaseFactoryService } from '@licence-management/infrastructure/usecase-factories/remove-licence-for-student-use-case-factory.service';
import { AddLicenceRequestDto } from '@licence-management/infrastructure/dto/add-licence-request.dto';
import { RemoveLicenceRequestDto } from '@licence-management/infrastructure/dto/remove-licence-request.dto';
import { Licence } from '@licences/domain/licence';
import { createLicenceFromLicenceDto } from './dto/licence.dto';

@Controller('v1/licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true }))
export class LicenceManagementController {
  constructor(
    private readonly addLicenceUseCaseFactory: AddLicenceForStudentUseCaseFactoryService,
    private readonly removeLicenceUseCaseFactory: RemoveLicenceForStudentUseCaseFactoryService,
  ) {}
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

    let licences: Licence[] = [];

    Object.keys(licencesToAdd).forEach((offer) => {
      licences = [
        ...licences,
        createLicenceFromLicenceDto(offer, licences[offer]),
      ];
    });

    const useCase = this.addLicenceUseCaseFactory.createLicenceRequestUseCase();

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
    description: 'Remove the licencenes for the specific user',
  })
  @ApiResponse({
    status: 400,
    description: 'Request does not match the expected schema',
  })
  public removeLicences(@Body() body: RemoveLicenceRequestDto) {
    const useCase =
      this.removeLicenceUseCaseFactory.createRemoveLicenceForStudentUseCase();

    useCase(
      body.studentId,
      body.licencesToRemove.map((licence) =>
        createLicenceFromLicenceDto('', licence),
      ),
    );
  }
}
