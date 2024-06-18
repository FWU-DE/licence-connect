import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { MVLicenceService } from './ucs/mv-license-service/mv-license-service.service';
import { MVLicenceFetcherService } from './ucs/mv-license-fetcher-service/mv-license-fetcher-service.service';

@Module({
  imports: [HttpModule],
  controllers: [LicencesController],
  providers: [MVLicenceService, MVLicenceFetcherService],
})
export class LicencesModule {}
