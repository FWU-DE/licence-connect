import { Controller, Get, Req } from '@nestjs/common';
import { IncomingLicenceRequest, LCLicences } from './licence-types';
import { MVLicenceService } from './mv-licence.service';

@Controller('licence')
export class LicencesController {
  constructor(private readonly mvLicenceService: MVLicenceService) {}

  // TODO: Add Authentication
  @Get()
  public getLicences(
    @Req() request: { body: IncomingLicenceRequest },
  ): LCLicences {
    const id = request.body.sub;
    return this.mvLicenceService.getLicences(id);
  }
}
