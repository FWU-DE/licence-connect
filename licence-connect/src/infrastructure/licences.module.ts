import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { UCSLicenceFetcherService as UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';

@Module({
  imports: [HttpModule],
  controllers: [LicencesController],
  providers: [UCSLicenseFetcherService],
})
export class LicencesModule {}
