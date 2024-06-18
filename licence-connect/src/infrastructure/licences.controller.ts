import { Controller, Get, Req } from '@nestjs/common';
import { RequestFromVidisCore } from './RequestFromVidisCore';
import { LicensesModel } from './LicencesModel';
import { MVLicenceFetcherService } from './ucs/mv-license-fetcher-service/mv-license-fetcher-service.service';
import { MVLicenceService } from './ucs/mv-license-service/mv-license-service.service';

@Controller('licence')
export class LicencesController {
  constructor(
    private readonly mvLicenceFetcherService: MVLicenceFetcherService,
    private readonly mvLicenceService: MVLicenceService,
  ) {}

  // TODO: Add Authentication
  @Get()
  public getLicences(
    @Req() request: { body: RequestFromVidisCore },
  ): LicensesModel {
    // TODO: use correct field for id
    const id = request.body.sub;
    const ucsStudentData = this.mvLicenceFetcherService.getLicenses(id);
    return new LicensesModel(
      this.mvLicenceService.extractLicenceData(ucsStudentData),
    );
  }
}
