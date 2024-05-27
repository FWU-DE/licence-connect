import { Controller, Get, Req } from '@nestjs/common';
import { IncomingLicenceRequest, LCLicences } from './types';
import { LicenceService } from './licences.service';

@Controller('licence')
export class LicencesController {
  constructor(private readonly licenceService: LicenceService) {}

  @Get()
  public getLicences(
    @Req() request: { body: IncomingLicenceRequest },
  ): LCLicences {
    const id = request.body.sub;
    return this.licenceService.getLicences(id);
  }
}
