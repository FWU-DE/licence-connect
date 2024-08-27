import {
  Body,
  Controller,
  HttpCode,
  Inject,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
  Version,
} from '@nestjs/common';
import { ApiSecurity, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { VidisRequestDto } from '@vidis/infrastructure/dto/vidis-request.dto';
import { VidisApiKeyGuard } from './authentication/vidis-api-key.guard';
import { LicencesDto } from './dto/licences.dto';
import { createReleaseLicenceForStudentUseCase } from '@vidis/usecases/release-licence-for-student-use-case';
import { createRequestLicencesForStudentUseCase } from '@vidis/usecases/request-licences-use-case';
import { LicenceStrategyFactory } from '@vidis/domain/licence-strategy-factory';

@Controller('licences')
@UsePipes(new ValidationPipe({ enableDebugMessages: true, whitelist: true }))
export class VidisController {
  constructor(
    @Inject('LicenceStrategyFactory')
    private readonly licenceStrategyFactory: LicenceStrategyFactory,
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
    const licenceStrategy =
      this.licenceStrategyFactory.determineLicenceStrategy();

    const useCase = createRequestLicencesForStudentUseCase(licenceStrategy);

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
    const useCase = createReleaseLicenceForStudentUseCase();

    useCase(body.userId, body.clientId);
  }
}
