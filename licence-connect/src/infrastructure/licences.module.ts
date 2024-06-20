import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { UCSLicenceFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';

@Module({
  imports: [HttpModule],
  controllers: [LicencesController],
  providers: [UCSLicenceFetcherService],
})
export class LicencesModule {}
