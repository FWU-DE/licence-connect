import { Body, Controller, HttpCode, Post, Version } from '@nestjs/common';
import { ApiSecurity } from '@nestjs/swagger';
4;
import { VidisRequestDto } from '@vidis/infrastructure/dto/vidis-request.dto';
import { UcsRepositoryService } from './repository/ucs-repository.service';
import { UCSStudent } from '@ucs/domain/ucs-types';

@Controller('ucs')
export class UcsController {
  constructor(private readonly ucsRepository: UcsRepositoryService) {}

  @Post('request')
  @HttpCode(200)
  @Version('1')
  @ApiSecurity('VIDIS-Core')
  // @UseGuards(VidisApiKeyGuard)
  public async getLicences(@Body() body: VidisRequestDto): Promise<UCSStudent> {
    return await this.ucsRepository.getLicenceObjectForStudentId(body.userId);
  }
}
