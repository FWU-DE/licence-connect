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
import { ApiSecurity, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { VidisRequestDto } from '@vidis/infrastructure/dto/vidis-request.dto';
import { ReleaseLicenceForStudentUseCaseFactoryService } from '@vidis/infrastructure/usecase-factories/release-licence-for-student-use-case-factory.service';
import { RequestLicenceFactoryService } from '@vidis/infrastructure/usecase-factories/request-licence-factory.service';
import { VidisApiKeyGuard } from './authentication/vidis-api-key.guard';
import { LicencesDto } from './dto/licences.dto';

@Controller('v1/licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true }))
export class VidisController {
  constructor(
    private readonly requestLicenceUseCaseFactory: RequestLicenceFactoryService,
    private readonly releaseLicencesUseCaseFactory: ReleaseLicenceForStudentUseCaseFactoryService,
  ) {}

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
    const useCase =
      this.requestLicenceUseCaseFactory.createLicenceRequestUseCase(
        body.bundesland,
        body.schulkennung,
        body.clientId,
      );

    const licencesForUser = useCase(body.userId, body.clientId);

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
    const useCase =
      this.releaseLicencesUseCaseFactory.createReleaseLicenceForStudentUseCase(
        body.bundesland,
        body.schulkennung,
        body.clientId,
      );

    useCase(body.userId, body.clientId);
  }
}
