import { Controller, Get, Req } from '@nestjs/common';
import { IncomingLicenceRequest, Licences } from './types';
import { LicenceService } from './licences.service';

@Controller('licence')
export class LicencesController {
  constructor(private readonly licenceService: LicenceService) {}

  @Get()
  public getLicences(
    @Req() request: { body: IncomingLicenceRequest },
  ): Licences {
    const id = request.body.sub;
    return this.licenceService.getLicences(id);
  }
}
