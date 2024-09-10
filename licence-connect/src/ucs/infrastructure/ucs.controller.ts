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
import { ApiOperation, ApiResponse, ApiSecurity } from '@nestjs/swagger';
import { UcsRepositoryService } from './repository/ucs-repository.service';
import { UcsStudentDto } from './dto/ucs-type.dto';
import { UcsApiKeyGuard } from './authentication/ucs-api-key.guard';
import { LCLogger } from '@cross-cutting-concerns/logging/domain/logger';
import { UcsConfigurationService } from './configuration/ucs-configuration.service';
import { UCSStudentFromUCSStudentId } from '@ucs/usecases/ucs-student-from-ucs-student-id';
import { LOGGER_TOKEN } from '@cross-cutting-concerns/logging/infrastructure/logger.module';
import { UcsRequestDto } from './dto/ucs-request.dto';

@Controller('ucs')
@UsePipes(new ValidationPipe({ whitelist: true }))
export class UcsController {
  constructor(
    private readonly ucsRepositoryService: UcsRepositoryService,
    private readonly ucsConfigurationService: UcsConfigurationService,
    @Inject(LOGGER_TOKEN) private readonly logger: LCLogger,
  ) {}

  @Post('request')
  @HttpCode(200)
  @Version('1')
  @ApiSecurity('ucs')
  @ApiOperation({
    description:
      'Request the licenses for a specific user in the context of an ucs-system.',
    tags: ['ucs'],
  })
  @ApiResponse({
    status: 200,
    description: 'Fetch all licences available for a specific user',
  })
  @ApiResponse({
    status: 404,
    description:
      'The specific user is not available in the specific ucs context',
  })
  @UseGuards(UcsApiKeyGuard)
  public async getLicences(
    @Body() body: UcsRequestDto,
  ): Promise<UcsStudentDto> {
    this.logger.debug('Received request for BiLo Licences');

    const provider = this.ucsConfigurationService.getUcsConfiguration();
    const ucsRepository =
      this.ucsRepositoryService.getUcsStudentRepository(provider);

    const useCase = new UCSStudentFromUCSStudentId();

    const studentId = body.userId;
    const schoolContextIdentifier = body.schulkennung;
    return await useCase.execute(
      ucsRepository,
      studentId,
      schoolContextIdentifier,
    );
  }
}
