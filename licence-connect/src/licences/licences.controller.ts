import { Controller, Get } from '@nestjs/common';
import { Licences } from './types';
import { LicenceService } from './licences.service';

@Controller('licence')
export class LicencesController {
  constructor(public readonly licenceService: LicenceService) {}

  @Get()
  public getLicences(): Licences {
    return this.licenceService.getLicences();
  }
}
