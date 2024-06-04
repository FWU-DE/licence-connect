import { Controller, Get, Req } from '@nestjs/common';
import { IncomingLicenseRequest, LCLicenses } from './licence-types';
import { MVLicenceFetcherService } from './mv/mv-licence-fetcher-service';

@Controller('licence')
export class LicencesController {
  constructor(private readonly mvLicenceService: MVLicenceFetcherService) {}

  // TODO: Add Authentication
  @Get()
  public getLicences(
    @Req() request: { body: IncomingLicenseRequest },
  ): LCLicenses {
    // TODO: use correct field for id
    const id = request.body.sub;
    return this.mvLicenceService.getLicenses(id);
  }
}
