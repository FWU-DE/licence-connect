import {
  Body,
  Controller,
  HttpCode,
  Post,
  UseGuards,
  Version,
} from '@nestjs/common';
import { ApiOperation, ApiSecurity } from '@nestjs/swagger';
import { VidisRequestDto } from '@vidis/infrastructure/dto/vidis-request.dto';
import { UcsRepositoryService } from './repository/ucs-repository.service';
import { UCSStudentDto } from './dto/ucs-type.dto';
import { UcsConfigurationService } from './configuration/ucs-configuration.service';
import { UcsApiKeyGuard } from './authentication/ucs-api-key.guard';

@Controller('ucs')
export class UcsController {
  constructor(
    private readonly ucsRepository: UcsRepositoryService,
    private readonly ucsConfigService: UcsConfigurationService,
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
  @UseGuards(UcsApiKeyGuard)
  public async getLicences(
    @Body() body: VidisRequestDto,
  ): Promise<UCSStudentDto> {
    return await this.ucsRepository.getLicenceObjectForStudentId(body.userId);
  }
}
